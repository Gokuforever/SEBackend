package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {

	PENDING(1), SHIPPED(2), DELIVERED(3);

	private int id;

}
