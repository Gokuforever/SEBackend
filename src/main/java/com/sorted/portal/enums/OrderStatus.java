package com.sorted.portal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {

	ACTIVE, PAID, EXPIRED, TERMINATED;
}
