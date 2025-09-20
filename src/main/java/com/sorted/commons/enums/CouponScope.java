package com.sorted.commons.enums;

public enum CouponScope {
    PUBLIC,        // Available to all users
    USER_SPECIFIC, // Only available to specifically assigned users
    TARGETED       // Available to users in eligibleUserIds list
}
