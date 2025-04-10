package com.sorted.commons.utils;

import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.InvalidArgumentException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class Preconditions {

    public static void check(boolean condition, ResponseCode responseCode) {
        if (!condition) {
            throw new InvalidArgumentException(responseCode);
        }
    }

    public static <T extends RuntimeException> void check(boolean condition, T exception) {
        if (!condition) {
            throw exception;
        }
    }

    public static void main(String[] args) {
        check(BigDecimal.ZERO.compareTo(new BigDecimal(1)) == -1, ResponseCode.MISSING_ORDER_ID);
    }
}
