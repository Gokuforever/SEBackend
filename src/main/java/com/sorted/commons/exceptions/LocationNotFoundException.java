package com.sorted.commons.exceptions;

import com.sorted.commons.enums.ResponseCode;

public class LocationNotFoundException extends CustomIllegalArgumentsException {
    public LocationNotFoundException() {
        super(ResponseCode.LOCATION_NOT_FOUND);
    }
}
