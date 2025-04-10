package com.sorted.commons.exceptions;

import com.sorted.commons.enums.ResponseCode;

public class AccessDeniedException extends CustomIllegalArgumentsException {
    public AccessDeniedException() {
        super(ResponseCode.ACCESS_DENIED);
    }
}
