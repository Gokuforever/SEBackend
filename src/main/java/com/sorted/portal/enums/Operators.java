package com.sorted.portal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Operators {

	EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN, LIKE, IN, NOT_LIKE, ALL, ELEMMATCH_IN;
}
