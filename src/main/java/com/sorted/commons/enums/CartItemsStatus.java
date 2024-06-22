package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CartItemsStatus {

	ADDED(1);

	/*
	 * This is for next phase SAVED_FOR_LATER(2);
	 */
	private int id;
}
