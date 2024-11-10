package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {

	//@formatter:off
	

	
	ORDER_PLACED(0, "Order Placed", "Order Placed"),
	TRANSACTION_PROCESSED(1, "Order Placed", "Payment Processed"),
	ORDER_ACCEPTED(2, "Order Confirmed", "Seller Accepted"),
	READY_FOR_PICK_UP(3, "Ready For Pick Up", "Ready For Pick Up"), // SELLER - Ready for pick up
	RIDER_ASSIGNED(4, "Delivery Partner Assigned", "Delivery Partner Assigned"), // Delivery partner accepts the order
	OUT_FOR_DELIVERY(5, "Out For Delivery", "Out For Delivery"), 
	DELIVERED(6, "Delivered", "Delivered"),
	TRANSACTION_FAILED(7, "Payment Failed","Payment Failed"),
	TRANSACTION_PENDING(8, "Payment Pending", "Payment Pending"),
	FULLY_REFUNDED(9, "Fully Refunded", "Fully Refunded"), // 
	PARTIALLY_REFUNDED(10, "Partially Refunded", "Partially Refunded"),
	ORDER_REJECTED(11, "Order Rejected", "Order Rejected"), // Initiate complete refund
	ORDER_CANCELLED(12, "Order Cancelled", "Order Cancelled"); // Once cancelled - Complete amount refunded
	// @formatter:on

	private int id;
	private String customer_status;
	private String internal_status;

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
