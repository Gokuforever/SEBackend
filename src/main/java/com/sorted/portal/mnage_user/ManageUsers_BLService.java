package com.sorted.portal.mnage_user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sorted.portal.beans.OtpVerificationReqBean;
import com.sorted.portal.beans.UsersCUDReqBean;
import com.sorted.portal.constants.Defaults;
import com.sorted.portal.entity.mongo.BaseMongoEntity;
import com.sorted.portal.entity.mongo.Customer_Leads;
import com.sorted.portal.entity.mongo.Otp;
import com.sorted.portal.entity.mongo.Role;
import com.sorted.portal.entity.mongo.Users;
import com.sorted.portal.entity.service.Customer_Leads_Service;
import com.sorted.portal.entity.service.Otp_Service;
import com.sorted.portal.entity.service.RoleService;
import com.sorted.portal.entity.service.Users_Service;
import com.sorted.portal.enums.ProcessType;
import com.sorted.portal.enums.ResponseCode;
import com.sorted.portal.exceptions.CustomIllegalArgumentsException;
import com.sorted.portal.helper.AggregationFilter.SEFilter;
import com.sorted.portal.helper.AggregationFilter.SEFilterType;
import com.sorted.portal.helper.AggregationFilter.WhereClause;
import com.sorted.portal.helper.SERequest;
import com.sorted.portal.helper.SEResponse;
import com.sorted.portal.otp_service.ManageOtp;
import com.sorted.portal.utils.PasswordValidatorUtils;
import com.sorted.portal.utils.SERegExpUtils;

@RestController
public class ManageUsers_BLService {

//	private static final Logger logger = LoggerFactory.getLogger(ManageUsers_BLService.class);

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	private Users_Service users_Service;

	@Autowired
	private RoleService roleService;

	@Autowired
	private Customer_Leads_Service customer_Leads_Service;

	@Autowired
	private Otp_Service otp_Service;

	@Autowired
	private ManageOtp manageOtp;

//	@Autowired
//	private ManageSMS_BLService manageSMS_BLService;

	@PostMapping("/signup")
	public SEResponse signup(@RequestBody SERequest request) {

		UsersCUDReqBean req = request.getGenericRequestDataObject(UsersCUDReqBean.class);
		if (!StringUtils.hasText(req.getFirst_name())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_FN);
		}
		if (!StringUtils.hasText(req.getLast_name())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_LN);
		}
		if (!StringUtils.hasText(req.getMobile_no())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_MN);
		}
		if (!StringUtils.hasText(req.getEmail_id())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_EI);
		}
		if (!StringUtils.hasText(req.getStream())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_STREAM);
		}
		if (!StringUtils.hasText(req.getSemister())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_SEMISTER);
		}
		if (!StringUtils.hasText(req.getPassword())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_PASS);
		}
		if (!SERegExpUtils.isString(req.getFirst_name())) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_FN);
		}
		if (!SERegExpUtils.isString(req.getLast_name())) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_LN);
		}
		if (!SERegExpUtils.isMobileNo(req.getMobile_no())) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_MN);
		}
		if (!SERegExpUtils.isEmail(req.getEmail_id())) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_EI);
		}
		

		PasswordValidatorUtils.validatePassword(req.getPassword().trim());

		String encode = passwordEncoder.encode(req.getPassword().trim());

		SEFilter filterM = new SEFilter(SEFilterType.AND);
		filterM.addClause(WhereClause.eq(Users.Fields.mobile_no, req.getMobile_no()));
		filterM.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		long mobileCount = users_Service.countByFilter(filterM);
		if (mobileCount > 0) {
			throw new CustomIllegalArgumentsException(ResponseCode.DUPLICATE_MOBILE);
		}

		SEFilter filterE = new SEFilter(SEFilterType.AND);
		filterE.addClause(WhereClause.eq(Users.Fields.email_id, req.getEmail_id()));
		filterE.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		long emailCount = users_Service.countByFilter(filterE);
		if (emailCount > 0) {
			throw new CustomIllegalArgumentsException(ResponseCode.DUPLICATE_EMAIL);
		}

		Customer_Leads leads = new Customer_Leads();

		leads.setFirst_name(req.getFirst_name());
		leads.setLast_name(req.getLast_name());
		leads.setMobile_no(req.getMobile_no());
		leads.setEmail_id(req.getEmail_id());
		leads.setPassword(encode);

		leads = customer_Leads_Service.create(leads, Defaults.SIGN_UP);

		SEFilter filterO = new SEFilter(SEFilterType.AND);
		filterO.addClause(WhereClause.eq(Otp.Fields.mobile_no, req.getMobile_no()));
		filterO.addClause(WhereClause.eq(Otp.Fields.status, true));
		filterO.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		List<Otp> listOtp = otp_Service.repoFind(filterO);
		if (!CollectionUtils.isEmpty(listOtp)) {
			for (Otp otp : listOtp) {
				otp.setStatus(false);
				otp_Service.update(otp.getId(), otp, Defaults.SIGN_UP);
			}
		}

		manageOtp.send(req.getMobile_no(), leads.getId(), ProcessType.SIGN_UP);
//		Otp otp = new Otp();
////		int random_otp = CommonUtils.generateFixedLengthRandomNumber(6);
//		otp.setOtp_value("111111");
//		otp.setStatus(true);
//		otp.setExpiry_time(LocalDateTime.now().plusMinutes(3));
//		otp.setMobile_no(req.getMobile_no());
//		otp.setEntity_id(leads.getId());
//		otp.setProcess_type(ProcessType.SIGN_UP);
//		otp.setIs_verified(false);
//
//		otp_Service.create(otp, Defaults.SIGN_UP);
//
////		String authToken = "7wQ8MBamSXWG3qx9hYA2yulEkOV4HcZtrCjvfLUF0PobKJgen5B13FPMC6StpAEH7jzWQuahVGfRvYlk";
//
//		String verificationCode = "111111";
//		manageSMS_BLService.sendSMS(req.getMobile_no(), verificationCode);
//		String body = "{\r\n    \"route\": \"otp\",\r\n    \"variables_values\": \"" + verificationCode
//				+ "\",\r\n    \"numbers\": \"" + mobileNumber + "\"\r\n}";
//		WebClient webClient = WebClient.create("https://www.fast2sms.com/dev/bulkV2");
//		String response = webClient.post().uri("").header(HttpHeaders.AUTHORIZATION, sms_auth_token)
//				.contentType(MediaType.APPLICATION_JSON).bodyValue(body).retrieve().bodyToMono(String.class).block();
//		System.out.println(response);

		return SEResponse.getBasicSuccessResponseObject(leads.getId(), ResponseCode.SUCCESSFUL);
	}

	@PostMapping("/signup/verify")
	public SEResponse verify(@RequestBody SERequest request) {

		OtpVerificationReqBean req = request.getGenericRequestDataObject(OtpVerificationReqBean.class);
		String otp = req.getOtp();
		String entity_id = req.getEntity_id();
		if (!StringUtils.hasText(otp)) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_OTP);
		}
		if (!SERegExpUtils.isOtp(otp)) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_OTP);
		}
		if (!StringUtils.hasText(entity_id)) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_ENTITY);
		}

		SEFilter filterCL = new SEFilter(SEFilterType.AND);
		filterCL.addClause(WhereClause.eq(BaseMongoEntity.Fields.id, entity_id));
		filterCL.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		Customer_Leads leads = customer_Leads_Service.repoFindOne(filterCL);
		if (leads == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.ENTITY_NOT_FOUND);
		}

		manageOtp.verify(leads.getMobile_no(), entity_id, otp, ProcessType.SIGN_UP, Defaults.SIGN_UP);

//		SEFilter filterO = new SEFilter(SEFilterType.AND);
//		filterO.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
//		filterO.addClause(WhereClause.eq(Otp.Fields.entity_id, entity_id));
//		filterO.addClause(WhereClause.eq(Otp.Fields.mobile_no, leads.getMobile_no()));
//		filterO.addClause(WhereClause.eq(Otp.Fields.status, true));
//		filterO.addClause(WhereClause.eq(Otp.Fields.is_verified, false));
//		filterO.addClause(WhereClause.eq(Otp.Fields.otp_value, otp));
//		filterO.addClause(WhereClause.eq(Otp.Fields.process_type, ProcessType.SIGN_UP.name()));
//
//		OrderBy orderBy = new OrderBy();
//		orderBy.setKey(BaseMongoEntity.Fields.creation_date);
//		orderBy.setType(SortOrder.DESC);
//		filterO.setOrderBy(orderBy);
//
//		Otp otp2 = otp_Service.repoFindOne(filterO);
//		if (otp2 == null) {
//			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_OTP);
//		}
//		LocalDateTime now = LocalDateTime.now();
//		if (otp2.getExpiry_time().isBefore(LocalDateTime.now())) {
//			throw new CustomIllegalArgumentsException(ResponseCode.OTP_EXPIRED);
//		}
//		otp2.setIs_verified(true);
//		otp2.setVerification_time(now);
//		otp_Service.update(otp2.getId(), otp2, Defaults.SIGN_UP);

		SEFilter filterR = new SEFilter(SEFilterType.AND);
		filterR.addClause(WhereClause.eq(BaseMongoEntity.Fields.id, "65bf7de610296739a03f64de"));
		filterR.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		Role role = roleService.repoFindOne(filterR);
		if (role == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.DEFAULT_ROLE_MISSING);
		}

		Users users = new Users();
		users.setFirst_name(leads.getFirst_name());
		users.setLast_name(leads.getLast_name());
		users.setMobile_no(leads.getMobile_no());
		users.setEmail_id(leads.getEmail_id());
		users.setPassword(leads.getPassword());
		users.setIs_mobile_verified(true);
		users.setRole_id(role.getId());
		users.setRole_code(role.getCode());

		users_Service.create(users, Defaults.SIGN_UP);

		return SEResponse.getEmptySuccessResponse(ResponseCode.SUCCESSFUL);

	}
}
