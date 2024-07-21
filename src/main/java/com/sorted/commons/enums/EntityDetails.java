package com.sorted.commons.enums;

import java.util.HashMap;
import java.util.Map;

import com.sorted.commons.entity.mongo.Cart;
import com.sorted.commons.entity.mongo.Category_Master;
import com.sorted.commons.entity.mongo.Customer_Leads;
import com.sorted.commons.entity.mongo.Order_Details;
import com.sorted.commons.entity.mongo.Otp;
import com.sorted.commons.entity.mongo.Product_Category_Mapping;
import com.sorted.commons.entity.mongo.Products;
import com.sorted.commons.entity.mongo.Role;
import com.sorted.commons.entity.mongo.SmsPool;
import com.sorted.commons.entity.mongo.Third_Party_Api;
import com.sorted.commons.entity.mongo.Transaction_Details;
import com.sorted.commons.entity.mongo.Users;
import com.sorted.commons.entity.mongo.Varient_Mapping;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum EntityDetails {

	// @formatter:off
	CART(Cart.class),
	CATEGORY_MASTER(Category_Master.class),
	CUSTOMER_LEADS(Customer_Leads.class),
	ORDER_DETAILS(Order_Details.class),
	OTP(Otp.class),
	PRODUCT_CATEGORY_MAPPING(Product_Category_Mapping.class),
	PRODUCTS(Products.class),
	ROLE(Role.class),
	SMSPOOL(SmsPool.class),
	THIRD_PARTY_API(Third_Party_Api.class),
	TRANSACTION_DETAILS(Transaction_Details.class),
	USERS(Users.class),
	VARIENT_MAPPING(Varient_Mapping.class);
	// @formatter:on

	private Class<?> class_name;

	public static final Map<Class<?>, EntityDetails> byValue = new HashMap<>();

	static {
		for (EntityDetails ed : values()) {
			byValue.put(ed.getClass_name(), ed);
		}
	}

	public static void assertExists(@NonNull Class<?> clazz) {
		if (!byValue.containsKey(clazz)) {
			throw new CustomIllegalArgumentsException(ResponseCode.ENTITY_DEFINATION_INCOMPLETE);
		}
	}
}
