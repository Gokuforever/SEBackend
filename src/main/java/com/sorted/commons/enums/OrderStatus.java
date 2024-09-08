package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {

	//@formatter:off
	ORDER_REQUESTED(1, "PENDING","ORDER REQUESTED"),
	PAYMENT_PROCESSED(2, "PENDING","PAYMENT PROCESSED"),
	PAYMENT_FAILED(3, "PENDING","PAYMENT FAILED"),
	DELIVERY_REQUESTED(4, "ORDER CONFIRMED","DELIVERY REQUESTED"),
	SHIPPED(5, "ORDER SHIPPED","ORDER SHIPPED"), 
	OUT_FOR_DELIVERY(6, "OUT FOR DELIVERY","OUT FOR DELIVERY"), 
	DELIVERED(7, "DELIVERED","DELIVERED");
	// @formatter:on

	private int id;
	private String status;
	private String sub_status;

//	final static Map<String, Integer> getByStatus = new HashMap<>();
//	final static Map<Integer, String> getById = new HashMap<>();
//	static {
//		for (OrderStatus e : OrderStatus.values()) {
//			getById.put(e.getId(), e.getStatus());
//			getByStatus.put(e.getStatus(), e.getId());
//		}
//	}
//
//	public String getById(int i) {
//		if (getById.containsKey(i)) {
//			return getById.get(i);
//		} else {
//			return null;
//		}
//	}
//
//	public static int getByStatus(String s) {
//		if (getByStatus.containsKey(s)) {
//			return getByStatus.get(s);
//		} else {
//			return 0;
//		}
//	}
}
