package com.sorted.commons.exceptions;

import com.sorted.commons.enums.ResponseCode;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.io.Serial;

import static com.sorted.commons.enums.ResponseCode.RESTRICTED_LOCATION;

public class DeliveryNotAvailableException extends BaseException {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;

    public DeliveryNotAvailableException() {
        super(RESTRICTED_LOCATION);
    }

}
