package com.sorted.portal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserType {
	SUPER_ADMIN(1, "Super Admin"), CUSTOMER(2, "Customer"), SELLER(3, "Seller");

	private int id;
	private String user_type;
}
