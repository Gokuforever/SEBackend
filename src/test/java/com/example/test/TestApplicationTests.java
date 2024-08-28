package com.example.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sorted.commons.SEApplication;
import com.sorted.commons.notifications.EmailSenderImpl;

@SpringBootTest(classes = SEApplication.class)
class TestApplicationTests {

	@Autowired
	private EmailSenderImpl emailSenderImpl;

//	@Test
//	void emailSendEmail() {
//		System.out.println("Test is working");
//		emailSenderImpl.sendEmail("yogeshkhaire288@gmail.com", "Test", "This is a test mail.");
//	}
	@Test
	void emailSendEmailHtml() {
		System.out.println("Sned html");
		String htmlContent = "<!DOCTYPE html>\n" +
				"<html lang=\"en\">\n" +
				"<head>\n" +
				"    <meta charset=\"UTF-8\">\n" +
				"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
				"    <title>Welcome to Studeaze!</title>\n" +
				"</head>\n" +
				"<body style=\"font-family: Arial, sans-serif; line-height: 1.6;\">\n" +
				"    <table style=\"width: 100%; max-width: 600px; margin: auto; border-collapse: collapse;\">\n" +
				"        <tr>\n" +
				"            <td style=\"background-color: #f8f9fa; padding: 20px; text-align: center;\">\n" +
				"                <!-- Logo -->\n" +
				"                <img src=\"YOUR_LOGO_URL\" alt=\"Studeaze Logo\" style=\"max-width: 150px; margin-bottom: 20px;\">\n" +
				"                <h2>Welcome to Studeaze!</h2>\n" +
				"            </td>\n" +
				"        </tr>\n" +
				"        <tr>\n" +
				"            <td style=\"padding: 20px; background-color: #ffffff;\">\n" +
				"                <p>Dear {value},</p>\n" +
				"                <p>We are excited to have you join us and embark on a wonderful journey together. Below are your login credentials:</p>\n" +
				"                <p><strong>Username:</strong> {value}</p>\n" +
				"                <p><strong>Password:</strong> {value}</p>\n" +
				"                <p>Please click on the link below to sign in and start exploring all that we have to offer:</p>\n" +
				"                <p style=\"text-align: center;\">\n" +
				"                    <a href=\"http://www.sorted.in\" style=\"display: inline-block; padding: 10px 20px; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 5px;\">Sign In</a>\n" +
				"                </p>\n" +
				"                <p>If you have any questions or need assistance, feel free to reach out.</p>\n" +
				"            </td>\n" +
				"        </tr>\n" +
				"        <tr>\n" +
				"            <td style=\"background-color: #f8f9fa; padding: 20px; text-align: center;\">\n" +
				"                <p>Best regards,</p>\n" +
				"                <p>Yogesh Khaire<br>Studeaze Team</p>\n" +
				"            </td>\n" +
				"        </tr>\n" +
				"        <tr>\n" +
				"            <td style=\"padding: 20px; background-color: #ffffff; text-align: center; font-size: 0.9em; color: #666;\">\n" +
				"                <p><strong>Disclaimer:</strong> This email and any attachments are intended solely for the use of the individual or entity to whom they are addressed. If you have received this email in error, please notify the sender immediately and delete this email from your system. Unauthorized use, disclosure, or copying of the contents is strictly prohibited.</p>\n" +
				"            </td>\n" +
				"        </tr>\n" +
				"    </table>\n" +
				"</body>\n" +
				"</html>";

		emailSenderImpl.sendEmailHtmlTemplate("anandsuryawanshi66@gmail.com", "You have an order", htmlContent);
	}
}
