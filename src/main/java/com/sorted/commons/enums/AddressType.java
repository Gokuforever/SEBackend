package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AddressType {

	HOME(1, "Home"), HOSTEL(2, "Hostel"), STORE(3, "Store"), OTHER(10, "Other");

	private int id;
	private String type;
}
