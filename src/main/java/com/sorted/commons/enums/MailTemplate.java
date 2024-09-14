package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MailTemplate {
	SIGN_UP_COMPLETED("sign_up_completed.html", "Welcom to the world for StudEaze."),
	SELLER_WELCOME_MAIL("welcome_mail.html", "Welcom to StudEaze.");

	@Getter
	private String file_name;
	@Getter
	private String subject;
}