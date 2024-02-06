package com.sorted.portal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

	ERR_0001("SE_0001", "Something went wrong.", "Something went wrong."),
	INVALID_REQ("SE_0002", "Invalid request.", "Invalid request.");

	private final String code;
	private final String errorMessage;
	private final String userMessage;
}
