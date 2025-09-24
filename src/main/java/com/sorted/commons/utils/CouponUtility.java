package com.sorted.commons.utils;

import com.sorted.commons.beans.CartBean;
import com.sorted.commons.entity.mongo.BaseMongoEntity;
import com.sorted.commons.entity.mongo.CouponEntity;
import com.sorted.commons.enums.CouponScope;
import com.sorted.commons.enums.DiscountType;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import com.sorted.commons.helper.AggregationFilter.SEFilter;
import com.sorted.commons.helper.AggregationFilter.SEFilterType;
import com.sorted.commons.helper.AggregationFilter.WhereClause;
import com.sorted.commons.repository.mongo.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponUtility {

    @Autowired
    private CouponRepository couponRepository;

    /**
     * Validates if a coupon can be applied to a cart for a specific user
     *
     * @param cart   The cart to apply the coupon to
     * @param coupon The coupon entity to validate
     * @param userId The user ID attempting to use the coupon
     * @return The discount amount in paise if valid
     * @throws RuntimeException if validation fails
     */
    public Long validateCouponAndGetDiscount(CartBean cart, CouponEntity coupon, String userId) {

        // Check cart has items
        Preconditions.check(!CollectionUtils.isEmpty(cart.getCart_items()), ResponseCode.NO_ITEMS_IN_CART);

        long totalAmountInPaise = CommonUtils.rupeeToPaise(cart.getTotal_amount());
        LocalDateTime now = LocalDateTime.now();

        // Check if coupon is active
        Preconditions.check(coupon.isActive(), ResponseCode.COUPON_CODE_NOT_ACTIVE);

        // Check if coupon has started (now should be after or equal to start date)
        Preconditions.check(!now.isBefore(coupon.getStartDate()), ResponseCode.COUPON_CODE_NOT_STARTED);

        // Check if coupon has not expired (now should be before or equal to end date)
        Preconditions.check(!now.isAfter(coupon.getEndDate()), ResponseCode.COUPON_CODE_EXPIRED);

        // Check coupon scope
        CouponScope couponScope = coupon.getCouponScope();
        if (couponScope != null && couponScope.equals(CouponScope.USER_SPECIFIC)) {
            List<String> assignedToUsers = coupon.getAssignedToUsers();
            Preconditions.check(!CollectionUtils.isEmpty(assignedToUsers), ResponseCode.INVALID_COUPON_CODE);
            Preconditions.check(assignedToUsers.contains(userId), ResponseCode.INVALID_COUPON_CODE);
        }

        // Check minimum purchase amount if applicable
        if (coupon.getMinCartValue() != null && coupon.getMinCartValue() > 0) {
            Preconditions.check(
                    totalAmountInPaise >= coupon.getMinCartValue(),
                    new CustomIllegalArgumentsException("Minimum cart value of ₹" + CommonUtils.paiseToRupee(coupon.getMinCartValue()) + " required")
            );
        }

        // Check if coupon is once per user and already used
        if (coupon.isOncePerUser()) {
            if (!CollectionUtils.isEmpty(coupon.getCouponUsages())) {
                boolean alreadyUsed = coupon.getCouponUsages().stream()
                        .anyMatch(usage -> usage.getUserId().equals(userId));
                Preconditions.check(!alreadyUsed, ResponseCode.COUPON_CODE_ALREADY_USED);
            }
        }

        // Check max uses per user if applicable
        if (coupon.getMaxUsesPerUser() != null && coupon.getMaxUsesPerUser() > 0) {
            long userUsageCount = coupon.getCouponUsages() != null ?
                    coupon.getCouponUsages().stream()
                            .filter(usage -> usage.getUserId().equals(userId))
                            .count() : 0;
            Preconditions.check(
                    userUsageCount < coupon.getMaxUsesPerUser(),
                    ResponseCode.COUPON_CODE_ALREADY_USED
            );
        }

        // Check max total uses if applicable
        if (coupon.getMaxUses() != null && coupon.getUsedCount() != null) {
            Preconditions.check(
                    coupon.getUsedCount() < coupon.getMaxUses(),
                    new CustomIllegalArgumentsException("Coupon has reached maximum uses")
            );
        }

        // Calculate discount amount based on discount type
        Long discountAmount = 0L;
        DiscountType discountType = coupon.getDiscountType();

        Preconditions.check(discountType != null, ResponseCode.MISSING_COUPON_DISCOUNT_TYPE);

        switch (discountType) {
            case FIXED -> {
                // Fixed discount amount
                Preconditions.check(coupon.getDiscountValue() != null, ResponseCode.MISSING_COUPON_DISCOUNT_VALUE);
                discountAmount = coupon.getDiscountValue();
                // Ensure discount doesn't exceed total amount
                discountAmount = Math.min(discountAmount, totalAmountInPaise);
            }
            case PERCENTAGE -> {
                // Percentage discount
                long percentageInHundredths = coupon.getDiscountPercentage().multiply(BigDecimal.valueOf(100)).longValue();
                discountAmount = (totalAmountInPaise * percentageInHundredths) / 10000;

                // Apply max discount limit if applicable
                if (coupon.getMaxDiscount() != null && discountAmount > coupon.getMaxDiscount()) {
                    discountAmount = coupon.getMaxDiscount();
                }
            }
            case FREE_SHIPPING -> {
                if (cart.is_free_delivery()) {
                    throw new CustomIllegalArgumentsException(ResponseCode.DELIVERY_ALREADY_FREE);
                }
                cart.set_free_delivery(true);
                discountAmount = CommonUtils.rupeeToPaise(cart.getDelivery_charge());
            }
            default -> throw new CustomIllegalArgumentsException("Invalid discount type: " + discountType);
        }
        return discountAmount;
    }

    /**
     * Validates if a coupon code is valid for a given cart
     *
     * @param couponCode The coupon code to validate
     * @param cart       The cart to validate the coupon against
     * @return true if the coupon is valid for the cart, false otherwise
     */
    public boolean validateCouponByCode(String couponCode, CartBean cart) {
        try {
            // Check if coupon code is provided
            if (!StringUtils.hasText(couponCode)) {
                return false;
            }

            // Validate cart
            if (cart == null) {
                return false;
            }

            // Check if cart has items
            if (CollectionUtils.isEmpty(cart.getCart_items())) {
                return false;
            }

            // Check if cart has valid total amount
            if (cart.getTotal_amount() == null || BigDecimal.ZERO.compareTo(cart.getTotal_amount()) >= 0) {
                return false;
            }

            // Find coupon by code
            SEFilter filter = new SEFilter(SEFilterType.AND);
            filter.addClause(WhereClause.eq(CouponEntity.Fields.code, couponCode));
            filter.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

            CouponEntity coupon = couponRepository.repoFindOne(filter);

            // Check if coupon exists
            if (coupon == null) {
                return false;
            }

            LocalDateTime now = LocalDateTime.now();

            // Check if coupon is active
            if (!coupon.isActive()) {
                return false;
            }

            // Check if coupon has started
            if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
                return false;
            }

            // Check if coupon has not expired
            if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
                return false;
            }

            // Check minimum cart value requirement
            if (coupon.getMinCartValue() != null && coupon.getMinCartValue() > 0) {
                Long totalAmountInPaise = CommonUtils.rupeeToPaise(cart.getTotal_amount());
                if (totalAmountInPaise < coupon.getMinCartValue()) {
                    return false;
                }
            }

            // Check if coupon has reached maximum total uses
            if (coupon.getMaxUses() != null && coupon.getUsedCount() != null) {
                if (coupon.getUsedCount() >= coupon.getMaxUses()) {
                    return false;
                }
            }

            // Check if discount type is valid
            if (coupon.getDiscountType() == null) {
                return false;
            }

            // Check if discount value/percentage is set based on discount type
            switch (coupon.getDiscountType()) {
                case FIXED:
                    if (coupon.getDiscountValue() == null || coupon.getDiscountValue() <= 0) {
                        return false;
                    }
                    // Check if fixed discount is not greater than cart total
                    Long cartTotalInPaise = CommonUtils.rupeeToPaise(cart.getTotal_amount());
                    if (coupon.getDiscountValue() > cartTotalInPaise) {
                        // Discount exceeds cart total - this might be valid in some cases
                        // but the actual discount will be capped at cart total
                    }
                    break;
                case PERCENTAGE:
                    if (coupon.getDiscountPercentage() == null ||
                            coupon.getDiscountPercentage().compareTo(BigDecimal.ZERO) <= 0 ||
                            coupon.getDiscountPercentage().compareTo(BigDecimal.valueOf(100)) > 0) {
                        return false;
                    }
                    break;
                case FREE_SHIPPING:
                    // Check if cart has delivery charge for free shipping to be meaningful
                    if (cart.is_free_delivery()) {
                        // Already free delivery, coupon won't provide additional benefit
                        return false;
                    }
                    if (cart.getDelivery_charge() == null || BigDecimal.ZERO.compareTo(cart.getDelivery_charge()) >= 0) {
                        // No delivery charge to discount
                        return false;
                    }
                    break;
                default:
                    return false;
            }

            // All validations passed
            return true;

        } catch (Exception e) {
            // Log the error if needed
            // Return false for any unexpected errors
            return false;
        }
    }

    /**
     * Calculates the discount amount for a valid coupon applied to a cart
     * This method assumes the coupon is already validated and focuses only on calculation
     *
     * @param couponCode Validated coupon code
     * @param cart       The cart to apply the coupon to
     * @return The discount amount in paise, returns 0 if any error occurs
     */
    public Long calculateDiscountAmount(String couponCode, CartBean cart) {
        try {
            // Find coupon by code
            SEFilter filter = new SEFilter(SEFilterType.AND);
            filter.addClause(WhereClause.eq(CouponEntity.Fields.code, couponCode));
            filter.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

            CouponEntity coupon = couponRepository.repoFindOne(filter);
            if (coupon == null || cart == null || CollectionUtils.isEmpty(cart.getCart_items())) {
                return 0L;
            }

            long totalAmountInPaise = CommonUtils.rupeeToPaise(cart.getTotal_amount());
            if (totalAmountInPaise <= 0) {
                return 0L;
            }

            long discountAmount;

            // Get discount type
            DiscountType discountType = coupon.getDiscountType();
            if (discountType == null) {
                return 0L;
            }

            switch (discountType) {
                case FIXED -> {
                    // Fixed discount amount
                    if (coupon.getDiscountValue() == null || coupon.getDiscountValue() <= 0) {
                        return 0L;
                    }
                    discountAmount = coupon.getDiscountValue();

                    // Ensure discount doesn't exceed total amount
                    discountAmount = Math.min(discountAmount, totalAmountInPaise);
                }
                case PERCENTAGE -> {
                    // Percentage discount
                    if (coupon.getDiscountPercentage() == null ||
                            coupon.getDiscountPercentage().compareTo(BigDecimal.ZERO) <= 0) {
                        return 0L;
                    }

                    // Convert percentage to hundredths for precision (e.g., 10.5% becomes 1050)
                    long percentageInHundredths = coupon.getDiscountPercentage()
                            .multiply(BigDecimal.valueOf(100))
                            .longValue();

                    // Calculate discount amount
                    discountAmount = (totalAmountInPaise * percentageInHundredths) / 10000;

                    // Apply max discount limit if applicable
                    if (coupon.getMaxDiscount() != null && discountAmount > coupon.getMaxDiscount()) {
                        discountAmount = coupon.getMaxDiscount();
                    }

                    // Ensure discount doesn't exceed total amount
                    discountAmount = Math.min(discountAmount, totalAmountInPaise);
                }
                case FREE_SHIPPING -> {
                    // Check if delivery is already free
                    if (cart.is_free_delivery()) {
                        // Delivery is already free, no discount to apply
                        discountAmount = 0L;
                    } else {
                        // Apply free shipping by returning delivery charge as discount
                        discountAmount = CommonUtils.rupeeToPaise(cart.getDelivery_charge());
                    }
                }
                default -> {
                    // Invalid discount type, return 0
                    return 0L;
                }
            }

            // Ensure discount is not negative
            discountAmount = Math.max(0L, discountAmount);

            return discountAmount;

        } catch (Exception e) {
            // Log error if needed
            // Return 0 for any unexpected errors
            return 0L;
        }
    }
}

