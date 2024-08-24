package com.sorted.commons.notifications;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class EmailSender {

    public static void main(String[] args) {
        // Gmail credentials
        final String username = "yogeshsorted@gmail.com";
        final String password = "sorted@yogesh";

        // Email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Get the Session object
        Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

        try {
            // Create a MimeMessage
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse("yogeshkhaire288@gmail.com")
            );
            message.setSubject("Your Subject Here");

            // Load HTML content from a file
            String htmlContent = new String(Files.readAllBytes(Paths.get("D:/Templates/welcome_mail.html")));

            // Set the HTML content
            message.setContent(htmlContent, "text/html");

            // Send the message
            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }
}
