package com.sorted.portal.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sorted.portal.constants.Defaults;
import com.sorted.portal.entity.mongo.SmsPool;
import com.sorted.portal.entity.service.SmsPool_Service;

import lombok.NonNull;

@Component
public class ManageSMS_BLService {

	private static final Logger logger = LoggerFactory.getLogger(ManageSMS_BLService.class);

	@Value("${fast2sms.auth.token}")
	private String sms_auth_token;

	@Autowired
	private SmsPool_Service smsPool_Service;

	public void sendSMS(@NonNull String mobileNumber, @NonNull String content) {

		SmsPool smsPool = new SmsPool();
		smsPool.setMobile_no(mobileNumber);
		smsPool.setContent(content);
		smsPool = smsPool_Service.create(smsPool, Defaults.SMS_SERVICE);
		if ("111111".equals(content)) {
			smsPool.setRaw_response("Static OTP");
			smsPool.set_sent(true);
		} else {
			try {
				String body = "{\r\n    \"route\": \"otp\",\r\n    \"variables_values\": \"" + content
						+ "\",\r\n    \"numbers\": \"" + mobileNumber + "\"\r\n}";
				WebClient webClient = WebClient.create("https://www.fast2sms.com/dev/bulkV2");
				String response = webClient.post().uri("").header(HttpHeaders.AUTHORIZATION, sms_auth_token)
						.contentType(MediaType.APPLICATION_JSON).bodyValue(body).retrieve().bodyToMono(String.class)
						.block();
				logger.info("response:: " + response);
				smsPool.setRaw_response(response);
				JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
				boolean is_sent = jsonObject.get("return").getAsBoolean();
				smsPool.set_sent(is_sent);

			} catch (Exception e) {
				logger.error("Error occurred while extracting SMS response.");
				logger.error(e.toString());
			}
		}
//		String verificationCode = "111111";
//		String mobileNumber = "9867292392";
		smsPool_Service.update(smsPool.getId(), smsPool, Defaults.SMS_SERVICE);
	}
}
