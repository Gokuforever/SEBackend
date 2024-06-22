package com.sorted.portal.mnage_user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.sorted.portal.enums.EngineeringStreams;
import com.sorted.portal.enums.ProcessType;
import com.sorted.portal.enums.ResponseCode;
import com.sorted.portal.enums.UserType;
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
	
	@Value("${se.portal.default_role}")
	private String default_role_id;

	@PostMapping("/api/signup")
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
		String password = req.getPassword().trim();
		PasswordValidatorUtils.validatePassword(password);

		Customer_Leads leads = new Customer_Leads();

		if (req.getBranch() != null) {
			EngineeringStreams branch = EngineeringStreams.getById(req.getBranch());
			if (branch == null) {
				throw new CustomIllegalArgumentsException(ResponseCode.INVALID_BRANCH);
			}
			leads.setBranch(branch.getId());
		}
		if (req.getSemister() != null) {
			if (req.getSemister() < 0 || req.getSemister() > 8) {
				throw new CustomIllegalArgumentsException(ResponseCode.INVALID_SEMISTER);
			}
			leads.setSemister(req.getSemister());
		}

		String encode = passwordEncoder.encode(password);

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

		leads.setFirst_name(req.getFirst_name());
		leads.setLast_name(req.getLast_name());
		leads.setMobile_no(req.getMobile_no());
		leads.setEmail_id(req.getEmail_id());
		leads.setPassword(encode);
		leads.setUser_type(UserType.CUSTOMER);

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
		return SEResponse.getBasicSuccessResponseObject(leads.getId(), ResponseCode.SUCCESSFUL);
	}

	@PostMapping("/api/signup/verify")
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

		SEFilter filterR = new SEFilter(SEFilterType.AND);
		filterR.addClause(WhereClause.eq(BaseMongoEntity.Fields.id, default_role_id));
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
		users.setUser_type(leads.getUser_type());
		if (leads.getSemister() != null) {
			users.setSemister(leads.getSemister());
		}
		if (leads.getBranch() != null) {
			users.setBranch(leads.getBranch());
		}

		users_Service.create(users, Defaults.SIGN_UP);

		return SEResponse.getEmptySuccessResponse(ResponseCode.SUCCESSFUL);

	}
}
