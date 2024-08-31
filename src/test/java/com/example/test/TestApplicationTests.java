package com.example.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sorted.commons.SEApplication;
import com.sorted.commons.enums.MailTemplate;
import com.sorted.commons.helper.MailBuilder;
import com.sorted.commons.notifications.EmailSenderImpl;

@SpringBootTest(classes = SEApplication.class)
class TestApplicationTests {

	@Autowired
	private EmailSenderImpl emailSenderImpl;

	@Test
	void emailSendEmailHtml() {

		String cont = "Yogesh|1st Sept 2024";
		MailBuilder builder = new MailBuilder();
		builder.setTo("yogeshkhaire288@gmail.com");
		builder.setContent(cont);
		builder.setTemplate(MailTemplate.SIGN_UP_COMPLETED);
		emailSenderImpl.sendEmailHtmlTemplate(builder);
	}
}
