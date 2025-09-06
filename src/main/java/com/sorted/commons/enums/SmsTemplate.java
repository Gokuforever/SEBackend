package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SmsTemplate {

    OTP( "197532"),
    NEW_ORDER( "197532"),
    ;

    private final String templateId;
}
