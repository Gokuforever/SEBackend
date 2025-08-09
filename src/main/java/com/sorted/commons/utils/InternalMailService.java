package com.sorted.commons.utils;

import com.sorted.commons.enums.MailTemplate;
import com.sorted.commons.helper.MailBuilder;
import com.sorted.commons.notifications.EmailSenderImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternalMailService {

    private final List<String> internalEmails = Arrays.asList("support@studeaze.in", "yogeshkhaire288@gmail.com", "anandsuryawanshi66@gmail.com", "vinayakjopre@gmail.com");
    private final EmailSenderImpl emailSenderImpl;

    public void sendMailOnError(String summary, String subject, Exception e) {
        String errorContent = String.format("%s|%s|%s", summary, LocalDateTime.now(), e.getMessage());
        MailBuilder builder = new MailBuilder();
        builder.setTo(internalEmails);
        builder.setContent(errorContent);
        builder.setTemplate(MailTemplate.ERROR);
        builder.setSubject(subject);
        emailSenderImpl.sendEmailHtmlTemplate(builder);
    }
}
