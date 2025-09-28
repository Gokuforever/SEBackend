package com.sorted.commons.exceptions;

import com.sorted.commons.enums.ResponseCode;

public class InvalidReferralCodeException extends CustomIllegalArgumentsException {
    public InvalidReferralCodeException() {
        super(ResponseCode.INVALID_REFERRAL_CODE);
    }
}
