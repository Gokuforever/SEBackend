package com.sorted.commons.manage.otp;

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
import com.sorted.commons.helper.AggregationFilter.*;
import com.sorted.commons.utils.CommonUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

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
        String random_otp;
        if ("prod".equalsIgnoreCase(profile)) {
            random_otp = CommonUtils.generateFixedLengthRandomNumber(otp_length);
        } else {
            random_otp = "111111";
        }
        otp.setOtp_value(random_otp);
        otp.setStatus(true);
        otp.setExpiry_at(LocalDateTime.now().plusMinutes(5));
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
                // Approach 1: Using RestTemplate (Most reliable for form data)
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.set("Authorization", sms_auth_token);

                HttpEntity<MultiValueMap<String, String>> request = getMultiValueMapHttpEntity(mobileNumber, content, headers);
                String response = restTemplate.postForObject("https://www.fast2sms.com/dev/bulkV2", request, String.class);
                log.info("response:: " + response);
                smsPool.setRaw_response(response);
                if (response != null) {
                    JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                    boolean is_sent = jsonObject.get("return").getAsBoolean();
                    smsPool.set_sent(is_sent);
                }

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

    @NotNull
    private static HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(@NotNull String mobileNumber, @NotNull String content, HttpHeaders headers) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("sender_id", "STDZ");
        formData.add("message", "197532");
        formData.add("template_id", "1207175648734415953");
        formData.add("entity_id", "1201175208011209565");
        formData.add("route", "dlt");
        formData.add("numbers", mobileNumber);
        formData.add("variables_values", content);

        return new HttpEntity<>(formData, headers);
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
