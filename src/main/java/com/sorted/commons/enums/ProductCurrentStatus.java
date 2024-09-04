package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductCurrentStatus {
	IN_STOCK(1), OUT_OF_STOCK(2), IS_DELIVERABLE(3), CURRENTLY_UNAVAILABLE(4);

	private int status_id;
}