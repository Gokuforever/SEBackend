package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailTemplate {
	SIGN_UP_COMPLETED("sign_up_completed.html", "Welcome to the world of Studeaze."),
	DIRECT_ORDER_CONFIRMATION("direct_order_confirmation.html", "Order Confirmation!."),
	ORDER_ARRIVED("order_arrived.html", "Your order has arrived."),
	SELLER_WELCOME_MAIL("welcome_mail.html", "Welcome to Studeaze.");

	private final String file_name;
	private final String subject;
}