package com.sorted.commons.manage.otp;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sorted.commons.constants.Defaults;
import com.sorted.commons.entity.mongo.BaseMongoEntity;
import com.sorted.commons.entity.mongo.Otp;
import com.sorted.commons.entity.mongo.SmsPool;
import com.sorted.commons.entity.service.Otp_Service;
import com.sorted.commons.entity.service.SmsPool_Service;
import com.sorted.commons.enums.EntityDetails;
import com.sorted.commons.enums.ProcessType;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import com.sorted.commons.helper.AggregationFilter.OrderBy;
import com.sorted.commons.helper.AggregationFilter.SEFilter;
import com.sorted.commons.helper.AggregationFilter.SEFilterType;
import com.sorted.commons.helper.AggregationFilter.SortOrder;
import com.sorted.commons.helper.AggregationFilter.WhereClause;
import com.sorted.commons.utils.CommonUtils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ManageOtp {

	@Autowired
	private Otp_Service otp_Service;

	@Autowired
	private SmsPool_Service smsPool_Service;

	@Value("${spring.profiles.active}")
	private String profile;

	@Value("${se.portal.otp_length}")
	private int otp_length;

	@Value("${fast2sms.auth.token}")
	private String sms_auth_token;

	public String send(@NonNull String mobile_number, @NonNull String entity_id, @NonNull ProcessType process_type,
			EntityDetails entity, String cud_by) {
		SEFilter filterO = new SEFilter(SEFilterType.AND);
		filterO.addClause(WhereClause.eq(Otp.Fields.mobile_no, mobile_number));
		filterO.addClause(WhereClause.eq(Otp.Fields.status, true));
		filterO.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		List<Otp> listOtp = otp_Service.repoFind(filterO);
		if (!CollectionUtils.isEmpty(listOtp)) {
			for (Otp tempOtp : listOtp) {
				tempOtp.setStatus(false);
				otp_Service.update(tempOtp.getId(), tempOtp, cud_by);
			}
		}

		Otp otp = new Otp();
		String random_otp = null;
		if ("prod".equalsIgnoreCase(profile)) {
			random_otp = CommonUtils.generateFixedLengthRandomNumber(otp_length);
		} else {
			random_otp = "111111";
		}
		otp.setOtp_value(random_otp);
		otp.setStatus(true);
		otp.setExpiry_at(LocalDateTime.now().plusMinutes(3));
		otp.setMobile_no(mobile_number);
		otp.setEntity_id(entity_id);
		otp.setProcess_type(process_type);
		otp.setIs_verified(false);
		otp.setEntity_type(entity);

		otp = otp_Service.create(otp, cud_by);
		this.sendSMS(mobile_number, random_otp);
		return otp.getUuid();
	}

	public void verify(EntityDetails entity, @NonNull String uuid, @NonNull String otp, @NonNull String entity_id,
			@NonNull ProcessType processType, String cud_by) {
		SEFilter filterO = new SEFilter(SEFilterType.AND);
		filterO.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
		filterO.addClause(WhereClause.eq(Otp.Fields.entity_type, entity.name()));
		filterO.addClause(WhereClause.eq(Otp.Fields.entity_id, entity_id));
		filterO.addClause(WhereClause.eq(Otp.Fields.status, true));
		filterO.addClause(WhereClause.eq(Otp.Fields.is_verified, false));
		filterO.addClause(WhereClause.eq(Otp.Fields.otp_value, otp));
		filterO.addClause(WhereClause.eq(Otp.Fields.uuid, uuid));
		filterO.addClause(WhereClause.eq(Otp.Fields.process_type, processType.name()));

		OrderBy orderBy = new OrderBy(BaseMongoEntity.Fields.creation_date, SortOrder.DESC);
		filterO.setOrderBy(orderBy);

		Otp otp2 = otp_Service.repoFindOne(filterO);
		if (otp2 == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_OTP);
		}
		LocalDateTime now = LocalDateTime.now();
		if (otp2.getExpiry_at().isBefore(LocalDateTime.now())) {
			throw new CustomIllegalArgumentsException(ResponseCode.OTP_EXPIRED);
		}
		otp2.setIs_verified(true);
		otp2.setVerified_at(now);
		otp_Service.update(otp2.getId(), otp2, cud_by);
	}

	private void sendSMS(@NonNull String mobileNumber, @NonNull String content) {

		SmsPool smsPool = new SmsPool();
		smsPool.setMobile_no(mobileNumber);
		smsPool.setContent(content);
		smsPool = smsPool_Service.create(smsPool, Defaults.SMS_SERVICE);
		if ("prod".equalsIgnoreCase(profile)) {
			try {
				String body = "{\r\n    \"route\": \"otp\",\r\n    \"variables_values\": \"" + content
						+ "\",\r\n    \"numbers\": \"" + mobileNumber + "\"\r\n}";
				WebClient webClient = WebClient.create("https://www.fast2sms.com/dev/bulkV2");
				String response = webClient.post().uri("").header(HttpHeaders.AUTHORIZATION, sms_auth_token)
						.contentType(MediaType.APPLICATION_JSON).bodyValue(body).retrieve().bodyToMono(String.class)
						.block();
				log.info("response:: " + response);
				smsPool.setRaw_response(response);
				JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
				boolean is_sent = jsonObject.get("return").getAsBoolean();
				smsPool.set_sent(is_sent);

			} catch (Exception e) {
				log.error("Error occurred while extracting SMS response.");
				log.error(e.toString());
			}
		} else {
			smsPool.setRaw_response("Static OTP");
			smsPool.set_sent(true);
		}
		smsPool_Service.update(smsPool.getId(), smsPool, Defaults.SMS_SERVICE);
	}

	public String resendOtp(@NonNull ProcessType process, @NonNull String uuid, @NonNull String entity_id) {
		SEFilter filterO = new SEFilter(SEFilterType.AND);
		filterO.addClause(WhereClause.eq(Otp.Fields.process_type, process.name()));
		filterO.addClause(WhereClause.eq(Otp.Fields.entity_id, entity_id));
		filterO.addClause(WhereClause.eq(Otp.Fields.status, true));
		filterO.addClause(WhereClause.eq(Otp.Fields.is_verified, false));
		filterO.addClause(WhereClause.eq(Otp.Fields.uuid, uuid));
		filterO.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		Otp oldOtp = otp_Service.repoFindOne(filterO);
		if (oldOtp == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_RESEND_REQUEST);
		}
		oldOtp.setStatus(false);
		otp_Service.update(oldOtp.getId(), oldOtp, Defaults.RESEND);
		return this.send(oldOtp.getMobile_no(), oldOtp.getEntity_id(), process, oldOtp.getEntity_type(),
				Defaults.RESEND);
	}
}
