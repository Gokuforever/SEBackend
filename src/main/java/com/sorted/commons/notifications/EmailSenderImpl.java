package com.sorted.commons.notifications;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailSenderImpl implements EmailSender {

	@Value("${spring.mail.username}")
	private String sender_mail;

	private JavaMailSender mailSender;

	public EmailSenderImpl(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Async
	@Override
	public void sendEmail(String to, String subject, String message) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(to);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(message);
		simpleMailMessage.setFrom(sender_mail);
		mailSender.send(simpleMailMessage);
	}

	@Async
	@Override
	public void sendEmail(String[] to, String subject, String message) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(to);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(message);
		simpleMailMessage.setFrom(sender_mail);
		mailSender.send(simpleMailMessage);
	}

	@Async
	@Override
	public void sendEmailHtmlTemplate(String to, String subject, String html) {
		MimeMessage mimeMessage = mailSender.createMimeMessage();

		System.out.println("sendEmailHtmlTemplate");
		System.out.println(to);
		System.out.println(subject);
		System.out.println(html);
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			mimeMessageHelper.setTo(to);
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setFrom(sender_mail);
			mimeMessageHelper.setText(html, true);
			mailSender.send(mimeMessage);
			System.out.println("Sent");
		} catch (MessagingException e) {
			System.out.println("Error");
			e.printStackTrace();
		}

	}

}
