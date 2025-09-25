package com.sorted.commons.utils;

import com.sorted.commons.beans.CartBean;
import com.sorted.commons.beans.CartBeanV2;
import com.sorted.commons.beans.CouponCodeInfo;
import com.sorted.commons.entity.mongo.BaseMongoEntity;
import com.sorted.commons.entity.mongo.CouponEntity;
import com.sorted.commons.entity.service.CouponService;
import com.sorted.commons.enums.CouponScope;
import com.sorted.commons.enums.DiscountType;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import com.sorted.commons.helper.AggregationFilter.SEFilter;
import com.sorted.commons.helper.AggregationFilter.SEFilterType;
import com.sorted.commons.helper.AggregationFilter.WhereClause;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponUtility {

    @Value("${se.fixed-delivery-charge.in-paise:4100}")
    private long fixedDeliveryCharge;

    @Value("${se.minimum-cart-value.in-paise:39900}")
    private long minCartValueInPaise;

    @Value("${se.small-cart-fee.in-paise:1000}")
    private long smallCartFee;

    @Value("${se.handling-fee.in-paise:900}")
    private long handlingFee;


    private final CouponService couponService;

    public CouponCodeInfo validateCouponByCodeForCart(String code, Long totalSellingPriceInPaise, String userId) {
        CouponCodeInfo info = CouponCodeInfo.builder()
                .isValid(false)
                .isFreeDelivery(false)
                .discountAmount(0L)
                .build();

        if (!StringUtils.hasText(code)) {
            return info;
        }
        SEFilter filter = new SEFilter(SEFilterType.AND);
        filter.addClause(WhereClause.eq(CouponEntity.Fields.code, code));
        filter.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

        CouponEntity coupon = couponService.repoFindOne(filter);
        if (coupon == null) {
            return info;
        }

        LocalDateTime now = LocalDateTime.now();
        if (!coupon.isActive()) {
            return info;
        }
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            return info;
        }
        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            return info;
        }
        if (coupon.getMinCartValue() != null && coupon.getMinCartValue() > 0) {
            if (totalSellingPriceInPaise < coupon.getMinCartValue()) {
                return info;
            }
        }
        if (coupon.getMaxUses() != null && coupon.getUsedCount() != null) {
            if (coupon.getUsedCount() >= coupon.getMaxUses()) {
                return info;
            }
        }
        if (coupon.isOncePerUser()) {
            if (!CollectionUtils.isEmpty(coupon.getCouponUsages())) {
                boolean alreadyUsed = coupon.getCouponUsages().stream()
                        .anyMatch(usage -> usage.getUserId().equals(userId));
                if (alreadyUsed)
                    return info;
            }
        }
        if (coupon.getMaxUsesPerUser() != null && coupon.getMaxUsesPerUser() > 0) {
            long userUsageCount = coupon.getCouponUsages() != null ?
                    coupon.getCouponUsages().stream()
                            .filter(usage -> usage.getUserId().equals(userId))
                            .count() : 0;
            if (userUsageCount >= coupon.getMaxUsesPerUser()) {
                return info;
            }
        }

        if (coupon.getDiscountType() == null) {
            return info;
        }
        long discountAmount;
        boolean isFreeShipping = false;
        DiscountType discountType = coupon.getDiscountType();


        switch (discountType) {
            case FIXED -> {
                // Fixed discount amount
                if (coupon.getDiscountValue() == null) {
                    return info;
                }
                discountAmount = coupon.getDiscountValue();
                // Ensure discount doesn't exceed total amount
                discountAmount = Math.min(discountAmount, totalSellingPriceInPaise);
            }
            case PERCENTAGE -> {
                // Percentage discount
                if (coupon.getDiscountPercentage() == null) {
                    return info;
                }
                long percentageInHundredths = coupon.getDiscountPercentage().multiply(BigDecimal.valueOf(100)).longValue();
                discountAmount = (totalSellingPriceInPaise * percentageInHundredths) / 10000;

                // Apply max discount limit if applicable
                if (coupon.getMaxDiscount() != null && discountAmount > coupon.getMaxDiscount()) {
                    discountAmount = coupon.getMaxDiscount();
                }
            }
            default -> {
                if (totalSellingPriceInPaise > minCartValueInPaise) {
                    return info;
                }
                isFreeShipping = true;
                discountAmount = fixedDeliveryCharge + smallCartFee + handlingFee;
            }
        }

        return CouponCodeInfo.builder()
                .isValid(true)
                .isFreeDelivery(isFreeShipping)
                .discountAmount(discountAmount)
                .build();
    }

    /**
     * Validates if a coupon can be applied to a cart for a specific user
     *
     * @param toPay          The amount to pay
     * @param code           The coupon code to validate
     * @param userId         The user ID attempting to use the coupon
     * @param isFreeDelivery is free delivery
     * @throws RuntimeException if validation fails
     */
    public void validateCouponAndThrowException(String code, Long toPay, String userId, boolean isFreeDelivery) {

        LocalDateTime now = LocalDateTime.now();

        SEFilter filter = new SEFilter(SEFilterType.AND);
        filter.addClause(WhereClause.eq(CouponEntity.Fields.code, code));
        filter.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

        CouponEntity coupon = couponService.repoFindOne(filter);

        Preconditions.check(coupon != null, ResponseCode.INVALID_COUPON_CODE);

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
                    toPay >= coupon.getMinCartValue(),
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

        DiscountType discountType = coupon.getDiscountType();

        Preconditions.check(discountType != null, ResponseCode.MISSING_COUPON_DISCOUNT_TYPE);

        switch (discountType) {
            case FIXED -> {
                // Fixed discount amount
                Preconditions.check(coupon.getDiscountValue() != null, ResponseCode.MISSING_COUPON_DISCOUNT_VALUE);
                // Ensure discount doesn't exceed total amount
            }
            case PERCENTAGE -> {
                // Percentage discount
                long percentageInHundredths = coupon.getDiscountPercentage().multiply(BigDecimal.valueOf(100)).longValue();
            }
            case FREE_SHIPPING -> {
                if (isFreeDelivery) {
                    throw new CustomIllegalArgumentsException(ResponseCode.DELIVERY_ALREADY_FREE);
                }
            }
            default -> throw new CustomIllegalArgumentsException("Invalid discount type: " + discountType);
        }
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

            CouponEntity coupon = couponService.repoFindOne(filter);

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

            CouponEntity coupon = couponService.repoFindOne(filter);
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

