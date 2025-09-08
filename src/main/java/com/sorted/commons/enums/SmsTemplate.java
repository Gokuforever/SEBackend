package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SmsTemplate {

    OTP( "197876"),
    NEW_ORDER( "197875"),
    ;

    private final String templateId;
}
