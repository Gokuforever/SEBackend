package com.sorted.commons.notifications;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.sorted.commons.enums.MailTemplate;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import com.sorted.commons.helper.MailBuilder;

import jakarta.mail.internet.MimeMessage;

@Component
public class EmailSenderImpl {

	@Value("${spring.mail.username}")
	private String sender_mail;

	@Value("${se.email.template.base_folder}")
	private String email_base_folder;

	private JavaMailSender mailSender;

	public EmailSenderImpl(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

//	@Async
//	public void sendEmail(String to, String cc, String bcc, String subject, String message) {
//		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//		simpleMailMessage.setTo(to);
//		simpleMailMessage.setSubject(subject);
//		simpleMailMessage.setText(message);
//		simpleMailMessage.setFrom(sender_mail);
//		mailSender.send(simpleMailMessage);
//	}
//
//	@Async
//	public void sendEmail(String[] to, String[] cc, String[] bcc, String subject, String message) {
//		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//		simpleMailMessage.setTo(to);
//		simpleMailMessage.setSubject(subject);
//		simpleMailMessage.setText(message);
//		simpleMailMessage.setFrom(sender_mail);
//		mailSender.send(simpleMailMessage);
//	}

	@Async
	public void sendEmailHtmlTemplate(MailBuilder builder) {

		if (CollectionUtils.isEmpty(builder.getTo())) {
			throw new CustomIllegalArgumentsException(ResponseCode.RECIPIENT_MISSING);
		}

		MailTemplate template = builder.getTemplate();
		if (template == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.TEMPLATE_IS_MISSING);
		}

		String content = builder.getContent();
		if (!StringUtils.hasText(content)) {
			throw new CustomIllegalArgumentsException(ResponseCode.CONTENT_IS_MISSING);
		}
		String file_name = template.getFile_name();
		String str_template = loadTemplate(email_base_folder + file_name);
		if (str_template == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.ERR_0001);
		}

		str_template = this.replacePlaceholders(str_template, content);

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			mimeMessageHelper.setTo(builder.getTo().toArray(new String[0]));

			if (!CollectionUtils.isEmpty(builder.getCc())) {
				mimeMessageHelper.setCc(builder.getCc().toArray(new String[0]));
			}

			if (!CollectionUtils.isEmpty(builder.getBcc())) {
				mimeMessageHelper.setBcc(builder.getBcc().toArray(new String[0]));
			}

			mimeMessageHelper.setSubject(template.getSubject());
			mimeMessageHelper.setFrom(sender_mail);
			mimeMessageHelper.setText(str_template, true);

			mailSender.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String loadTemplate(String templateFilePath) {
		try {
			return new String(Files.readAllBytes(Paths.get(templateFilePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String replacePlaceholders(String str_template, String content) {

		String[] values = content.split("\\|");
		for (int i = 0; i < values.length; i++) {
			str_template = str_template.replace("{{a" + i + "}}", values[i]);
		}
		return str_template;
	}
}
