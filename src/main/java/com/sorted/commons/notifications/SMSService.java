package com.sorted.commons.notifications;

import com.sorted.commons.enums.SmsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SMSService {

    @Value("${fast2sms.auth.token}")
    private String sms_auth_token;

    private final RestTemplate restTemplate = new RestTemplate();

    public String sendSMS(List<String> mobileNumbers, String content, SmsTemplate template) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", sms_auth_token);

        String numbers = String.join(",", mobileNumbers);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("sender_id", "STDZ");
        formData.add("message", template.getTemplateId());
        formData.add("entity_id", "1201175208011209565");
        formData.add("route", "dlt");
        formData.add("numbers", numbers);
        formData.add("variables_values", content);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        return restTemplate.postForObject("https://www.fast2sms.com/dev/bulkV2", request, String.class);
    }
}
