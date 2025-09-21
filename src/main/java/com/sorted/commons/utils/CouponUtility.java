package com.sorted.commons.utils;

import com.sorted.commons.beans.CartBean;
import com.sorted.commons.entity.mongo.CouponEntity;
import com.sorted.commons.enums.CouponScope;
import com.sorted.commons.enums.DiscountType;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponUtility {

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

        Long totalAmountInPaise = CommonUtils.rupeeToPaise(cart.getTotal_amount());
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
}

