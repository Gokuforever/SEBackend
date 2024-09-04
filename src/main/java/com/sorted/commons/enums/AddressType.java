package com.sorted.commons.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum AddressType {

	HOME(1, "Home"), HOSTEL(2, "Hostel"), STORE(3, "Store"), OTHER(10, "Other");

	private int id;
	private String type;
	
	private static final Map<String, AddressType> ByName = new HashMap<>();
	static {
		for(AddressType a: values()) {
			ByName.put(a.name(), a);
		}
	}
	
	public static AddressType getByName(@NonNull String s) {
		try {
			return ByName.get(s.toUpperCase());
		} catch (Exception e) {
			return null;
		}
	}
}
