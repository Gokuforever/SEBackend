package com.sorted.commons.exceptions;

import com.sorted.commons.enums.ResponseCode;

public class InvalidDiscountType extends CustomIllegalArgumentsException {
    public InvalidDiscountType() {
        super(ResponseCode.MISSING_COUPON_DISCOUNT_VALUE);
    }
}