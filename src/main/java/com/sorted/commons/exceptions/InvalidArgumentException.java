package com.sorted.commons.exceptions;

import com.sorted.commons.enums.ResponseCode;

public class InvalidArgumentException extends CustomIllegalArgumentsException {
    public InvalidArgumentException(ResponseCode responseCode) {
        super(responseCode);
    }
}
