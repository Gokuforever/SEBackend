package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Activity {

	HOME(100,"Home"),
	PRODUCTS(101,"Products"),
	INVENTORY_MANAGEMENT(102,"InventoryManagement"),
	CART_MANAGEMENT(103,"CartManagement"),
	PURCHASE(104,"Purchase"),
	ORDER_MANAGEMENT(105,"OrderManagement"),
	SUBSCRIBE(106,"Subscribe"),
	USER_MANAGEMENT(107,"UserManagement");

	private int id;
	private String name;

}
