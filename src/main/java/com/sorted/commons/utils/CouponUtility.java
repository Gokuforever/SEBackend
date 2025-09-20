package com.sorted.commons.utils;

import com.sorted.commons.entity.mongo.CouponEntity;
import com.sorted.commons.enums.CouponScope;
import com.sorted.commons.enums.DiscountType;
import com.sorted.commons.enums.ResponseCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class CouponUtility {

    /**
     * Validates if a coupon is valid for use
     */
    public static boolean isValidCoupon(CouponEntity coupon) {
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

        // Check if coupon has expired
        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            return false;
        }

        // Check if coupon has reached max uses
        if (coupon.getMaxUses() != null && coupon.getUsedCount() != null
                && coupon.getUsedCount() >= coupon.getMaxUses()) {
            return false;
        }

        return true;
    }

    /**
     * Checks if a coupon is valid for a specific user
     */
    public static boolean isValidForUser(CouponEntity coupon, String userId) {
        if (!isValidCoupon(coupon)) {
            return false;
        }

        if (StringUtils.isBlank(userId)) {
            return false;
        }

        // Check coupon scope
        if (coupon.getCouponScope() != null) {
            switch (coupon.getCouponScope()) {
                case PUBLIC:
                    // Available to all users
                    break;
                case USER_SPECIFIC:
                    // Check if user is in assigned users list
                    if (CollectionUtils.isEmpty(coupon.getAssignedToUsers())
                            || !coupon.getAssignedToUsers().contains(userId)) {
                        return false;
                    }
                    break;
                case TARGETED:
                    // Check if user is in eligible users list
                    if (CollectionUtils.isEmpty(coupon.getEligibleUserIds())
                            || !coupon.getEligibleUserIds().contains(userId)) {
                        return false;
                    }
                    break;
            }
        }

        return true;
    }

    /**
     * Checks if a coupon can be applied to a purchase amount
     */
    public static boolean canApplyToPurchaseAmount(CouponEntity coupon, Long purchaseAmount) {
        if (coupon == null || purchaseAmount == null) {
            return false;
        }

        // Check minimum purchase amount
        if (coupon.getMinPurchaseAmount() != null) {
            try {
                Preconditions.check(
                    purchaseAmount >= coupon.getMinPurchaseAmount(),
                    new IllegalArgumentException("Purchase amount is less than minimum required amount")
                );
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        return true;
    }

    /**
     * Calculates discount amount based on coupon type
     */
    public static Long calculateDiscountAmount(CouponEntity coupon, Long purchaseAmount) {
        // Validate inputs using Preconditions
        try {
            Preconditions.check(coupon != null, ResponseCode.ENTITY_NOT_FOUND);
            Preconditions.check(purchaseAmount != null && purchaseAmount > 0, ResponseCode.INVALID_AMOUNT);
            Preconditions.check(coupon.getDiscountType() != null, ResponseCode.INVALID_REQ);
            Preconditions.check(coupon.getDiscountValue() != null, ResponseCode.INVALID_REQ);
        } catch (RuntimeException e) {
            return 0L;
        }

        Long discountAmount = 0L;

        switch (coupon.getDiscountType()) {
            case PERCENTAGE:
                // Calculate percentage discount (assuming discountValue is in percentage)
                discountAmount = (purchaseAmount * coupon.getDiscountValue()) / 100;
                break;
            case FIXED:
                // Fixed discount amount
                discountAmount = coupon.getDiscountValue();
                // Ensure discount doesn't exceed purchase amount
                if (discountAmount > purchaseAmount) {
                    discountAmount = purchaseAmount;
                }
                break;
            case FREE_SHIPPING:
                // For free shipping, return the discount value (shipping cost)
                discountAmount = coupon.getDiscountValue();
                break;
        }

        return discountAmount;
    }

    /**
     * Calculates final amount after applying coupon
     */
    public static Long calculateFinalAmount(CouponEntity coupon, Long purchaseAmount) {
        Long discountAmount = calculateDiscountAmount(coupon, purchaseAmount);
        if (purchaseAmount == null) {
            return 0L;
        }
        return Math.max(0, purchaseAmount - discountAmount);
    }

    /**
     * Generates a unique coupon code
     */
    public static String generateCouponCode(String prefix) {
        String uniquePart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        if (StringUtils.isNotBlank(prefix)) {
            return prefix.toUpperCase() + uniquePart;
        }
        return uniquePart;
    }

    /**
     * Checks if a coupon has expired
     */
    public static boolean hasExpired(CouponEntity coupon) {
        if (coupon == null || coupon.getEndDate() == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(coupon.getEndDate());
    }

    /**
     * Checks if a coupon has started
     */
    public static boolean hasStarted(CouponEntity coupon) {
        if (coupon == null || coupon.getStartDate() == null) {
            return true; // If no start date, assume it has started
        }
        return !LocalDateTime.now().isBefore(coupon.getStartDate());
    }

    /**
     * Gets remaining uses for a coupon
     */
    public static Integer getRemainingUses(CouponEntity coupon) {
        if (coupon == null || coupon.getMaxUses() == null) {
            return null; // Unlimited uses
        }

        Integer usedCount = coupon.getUsedCount() != null ? coupon.getUsedCount() : 0;
        return Math.max(0, coupon.getMaxUses() - usedCount);
    }

    /**
     * Checks if a user can still use the coupon based on per-user limits
     */
    public static boolean canUserUseCoupon(CouponEntity coupon, String userId, int userUsageCount) {
        if (coupon == null || StringUtils.isBlank(userId)) {
            return false;
        }

        // Check if it's a one-time use coupon per user
        if (coupon.isOncePerUser() && userUsageCount > 0) {
            return false;
        }

        // Check max uses per user
        if (coupon.getMaxUsesPerUser() != null && userUsageCount >= coupon.getMaxUsesPerUser()) {
            return false;
        }

        return true;
    }

    /**
     * Increments the used count of a coupon
     */
    public static void incrementUsedCount(CouponEntity coupon) {
        Preconditions.check(coupon != null, ResponseCode.ENTITY_NOT_FOUND);

        Integer currentCount = coupon.getUsedCount() != null ? coupon.getUsedCount() : 0;
        coupon.setUsedCount(currentCount + 1);
    }

    /**
     * Adds a user to the assigned users list
     */
    public static void assignToUser(CouponEntity coupon, String userId) {
        Preconditions.check(coupon != null, ResponseCode.ENTITY_NOT_FOUND);
        Preconditions.check(StringUtils.isNotBlank(userId), ResponseCode.MISSING_USER_ID);

        if (coupon.getAssignedToUsers() == null) {
            coupon.setAssignedToUsers(new ArrayList<>());
        }

        if (!coupon.getAssignedToUsers().contains(userId)) {
            coupon.getAssignedToUsers().add(userId);
        }
    }

    /**
     * Removes a user from the assigned users list
     */
    public static void unassignFromUser(CouponEntity coupon, String userId) {
        Preconditions.check(coupon != null, ResponseCode.ENTITY_NOT_FOUND);
        Preconditions.check(StringUtils.isNotBlank(userId), ResponseCode.MISSING_USER_ID);

        if (CollectionUtils.isNotEmpty(coupon.getAssignedToUsers())) {
            coupon.getAssignedToUsers().remove(userId);
        }
    }

    /**
     * Creates a coupon with basic validation
     */
    public static CouponEntity createCouponEntity(String code, String name, DiscountType discountType,
                                                  Long discountValue, LocalDateTime startDate,
                                                  LocalDateTime endDate, CouponScope scope) {
        // Validate required parameters
        Preconditions.check(StringUtils.isNotBlank(code), new IllegalArgumentException("Coupon code is required"));
        Preconditions.check(StringUtils.isNotBlank(name), new IllegalArgumentException("Coupon name is required"));
        Preconditions.check(discountType != null, new IllegalArgumentException("Discount type is required"));
        Preconditions.check(discountValue != null && discountValue > 0, new IllegalArgumentException("Valid discount value is required"));
        
        // Validate date consistency if both dates are provided
        if (startDate != null && endDate != null) {
            Preconditions.check(
                !endDate.isBefore(startDate),
                new IllegalArgumentException("End date cannot be before start date")
            );
        }
        
        return CouponEntity.builder()
                .code(code)
                .name(name)
                .discountType(discountType)
                .discountValue(discountValue)
                .startDate(startDate)
                .endDate(endDate)
                .couponScope(scope)
                .active(true)
                .usedCount(0)
                .build();
    }

    /**
     * Validates coupon dates
     */
    public static boolean hasValidDates(CouponEntity coupon) {
        if (coupon == null) {
            return false;
        }

        // If both dates are present, end date should be after start date
        if (coupon.getStartDate() != null && coupon.getEndDate() != null) {
            return !coupon.getEndDate().isBefore(coupon.getStartDate());
        }

        return true;
    }

    /**
     * Gets a formatted discount description
     */
    public static String getDiscountDescription(CouponEntity coupon) {
        if (coupon == null || coupon.getDiscountType() == null || coupon.getDiscountValue() == null) {
            return "No discount";
        }

        switch (coupon.getDiscountType()) {
            case PERCENTAGE:
                return coupon.getDiscountValue() + "% off";
            case FIXED:
                return "₹" + coupon.getDiscountValue() + " off";
            case FREE_SHIPPING:
                return "Free shipping";
            default:
                return "Unknown discount";
        }
    }

    /**
     * Checks if the coupon requires a minimum purchase
     */
    public static boolean hasMinimumPurchaseRequirement(CouponEntity coupon) {
        return coupon != null && coupon.getMinPurchaseAmount() != null && coupon.getMinPurchaseAmount() > 0;
    }

    /**
     * Validates if a coupon code format is valid
     */
    public static boolean isValidCouponCode(String code) {
        try {
            // Check if code is not blank
            Preconditions.check(
                StringUtils.isNotBlank(code),
                new IllegalArgumentException("Coupon code cannot be blank")
            );
            
            // Check code length (between 3 and 20 characters)
            Preconditions.check(
                code.length() >= 3 && code.length() <= 20,
                new IllegalArgumentException("Coupon code must be between 3 and 20 characters")
            );
            
            // Check if code contains only alphanumeric characters (and optionally hyphens/underscores)
            Preconditions.check(
                code.matches("^[A-Za-z0-9_-]+$"),
                new IllegalArgumentException("Coupon code can only contain alphanumeric characters, hyphens and underscores")
            );
            
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Creates a copy of a coupon for duplication purposes
     */
    public static CouponEntity duplicateCoupon(CouponEntity original, String newCode) {
        Preconditions.check(original != null, ResponseCode.ENTITY_NOT_FOUND);
        Preconditions.check(StringUtils.isNotBlank(newCode), new IllegalArgumentException("New coupon code is required"));

        return CouponEntity.builder()
                .code(newCode)
                .name(original.getName())
                .description(original.getDescription())
                .discountType(original.getDiscountType())
                .discountValue(original.getDiscountValue())
                .maxUses(original.getMaxUses())
                .maxUsesPerUser(original.getMaxUsesPerUser())
                .startDate(original.getStartDate())
                .endDate(original.getEndDate())
                .minPurchaseAmount(original.getMinPurchaseAmount())
                .eligibleUserIds(original.getEligibleUserIds() != null ? new ArrayList<>(original.getEligibleUserIds()) : null)
                .couponScope(original.getCouponScope())
                .oncePerUser(original.isOncePerUser())
                .active(original.isActive())
                .usedCount(0)
                .build();
    }

    /**
     * Gets the effective discount percentage for display purposes
     */
    public static Double getEffectiveDiscountPercentage(CouponEntity coupon, Long purchaseAmount) {
        try {
            Preconditions.check(coupon != null, ResponseCode.ENTITY_NOT_FOUND);
            Preconditions.check(purchaseAmount != null && purchaseAmount > 0, ResponseCode.INVALID_AMOUNT);
        } catch (RuntimeException e) {
            return 0.0;
        }

        Long discountAmount = calculateDiscountAmount(coupon, purchaseAmount);
        return (discountAmount.doubleValue() / purchaseAmount.doubleValue()) * 100;
    }

    /**
     * Gets validation error message for a coupon
     */
    public static String getValidationError(CouponEntity coupon, String userId, Long purchaseAmount) {
        if (coupon == null) {
            return "Coupon not found";
        }

        if (!coupon.isActive()) {
            return "Coupon is not active";
        }

        LocalDateTime now = LocalDateTime.now();

        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            return "Coupon has not started yet";
        }

        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            return "Coupon has expired";
        }

        if (coupon.getMaxUses() != null && coupon.getUsedCount() != null
                && coupon.getUsedCount() >= coupon.getMaxUses()) {
            return "Coupon has reached maximum uses";
        }

        if (StringUtils.isNotBlank(userId) && coupon.getCouponScope() != null) {
            switch (coupon.getCouponScope()) {
                case USER_SPECIFIC:
                    if (CollectionUtils.isEmpty(coupon.getAssignedToUsers())
                            || !coupon.getAssignedToUsers().contains(userId)) {
                        return "Coupon is not available for this user";
                    }
                    break;
                case TARGETED:
                    if (CollectionUtils.isEmpty(coupon.getEligibleUserIds())
                            || !coupon.getEligibleUserIds().contains(userId)) {
                        return "User is not eligible for this coupon";
                    }
                    break;
            }
        }

        if (purchaseAmount != null && coupon.getMinPurchaseAmount() != null
                && purchaseAmount < coupon.getMinPurchaseAmount()) {
            return "Minimum purchase amount of ₹" + coupon.getMinPurchaseAmount() + " required";
        }

        return null; // No validation errors
    }
}
