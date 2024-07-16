package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserType {
	GUEST(0, "Guest"), SUPER_ADMIN(1, "Super Admin"), CUSTOMER(2, "Customer"), SELLER(3, "Seller");

	private int id;
	private String user_type;
}
