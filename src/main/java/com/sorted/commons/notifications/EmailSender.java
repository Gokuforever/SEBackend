package com.sorted.commons.notifications;

public interface EmailSender {

	void sendEmail(String to, String subject, String message);

	void sendEmail(String[] to, String subject, String message);

	void sendEmailHtmlTemplate(String to, String subject, String html);
}
