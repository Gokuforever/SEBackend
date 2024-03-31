package com.sorted.portal.otp_service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sorted.portal.constants.Defaults;
import com.sorted.portal.entity.mongo.BaseMongoEntity;
import com.sorted.portal.entity.mongo.Otp;
import com.sorted.portal.entity.service.Otp_Service;
import com.sorted.portal.enums.ProcessType;
import com.sorted.portal.enums.ResponseCode;
import com.sorted.portal.exceptions.CustomIllegalArgumentsException;
import com.sorted.portal.helper.AggregationFilter.OrderBy;
import com.sorted.portal.helper.AggregationFilter.SEFilter;
import com.sorted.portal.helper.AggregationFilter.SEFilterType;
import com.sorted.portal.helper.AggregationFilter.SortOrder;
import com.sorted.portal.helper.AggregationFilter.WhereClause;
import com.sorted.portal.notifications.ManageSMS_BLService;

import lombok.NonNull;

@Component
public class ManageOtp {

	@Autowired
	private Otp_Service otp_Service;

	@Autowired
	private ManageSMS_BLService manageSMS_BLService;

	public void send(@NonNull String mobileNumber, @NonNull String entity_id, @NonNull ProcessType processType) {
		Otp otp = new Otp();
//		int random_otp = CommonUtils.generateFixedLengthRandomNumber(6);
		String random_otp = "111111";
		otp.setOtp_value(random_otp);
		otp.setStatus(true);
		otp.setExpiry_time(LocalDateTime.now().plusMinutes(3));
		otp.setMobile_no(mobileNumber);
		otp.setEntity_id(entity_id);
		otp.setProcess_type(processType);
		otp.setIs_verified(false);

		otp_Service.create(otp, Defaults.SIGN_UP);

		manageSMS_BLService.sendSMS(mobileNumber, random_otp);

	}

	public void verify(@NonNull String mobile_no, @NonNull String entity_id, @NonNull String otp,
			@NonNull ProcessType processType, String cud_by) {
		SEFilter filterO = new SEFilter(SEFilterType.AND);
		filterO.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
		filterO.addClause(WhereClause.eq(Otp.Fields.entity_id, entity_id));
		filterO.addClause(WhereClause.eq(Otp.Fields.mobile_no, mobile_no));
		filterO.addClause(WhereClause.eq(Otp.Fields.status, true));
		filterO.addClause(WhereClause.eq(Otp.Fields.is_verified, false));
		filterO.addClause(WhereClause.eq(Otp.Fields.otp_value, otp));
		filterO.addClause(WhereClause.eq(Otp.Fields.process_type, processType.name()));

		OrderBy orderBy = new OrderBy();
		orderBy.setKey(BaseMongoEntity.Fields.creation_date);
		orderBy.setType(SortOrder.DESC);
		filterO.setOrderBy(orderBy);

		Otp otp2 = otp_Service.repoFindOne(filterO);
		if (otp2 == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_OTP);
		}
		LocalDateTime now = LocalDateTime.now();
		if (otp2.getExpiry_time().isBefore(LocalDateTime.now())) {
			throw new CustomIllegalArgumentsException(ResponseCode.OTP_EXPIRED);
		}
		otp2.setIs_verified(true);
		otp2.setVerification_time(now);
		otp_Service.update(otp2.getId(), otp2, cud_by);
	}
}
