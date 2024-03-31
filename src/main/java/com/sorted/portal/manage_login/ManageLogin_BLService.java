package com.sorted.portal.manage_login;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.sorted.portal.beans.LoginReqBean;
import com.sorted.portal.beans.UsersBean;
import com.sorted.portal.beans.VerifyOtpReqBean;
import com.sorted.portal.constants.Defaults;
import com.sorted.portal.entity.mongo.BaseMongoEntity;
import com.sorted.portal.entity.mongo.Role;
import com.sorted.portal.entity.mongo.Users;
import com.sorted.portal.entity.service.RoleService;
import com.sorted.portal.entity.service.Users_Service;
import com.sorted.portal.enums.All_Status.User_Status;
import com.sorted.portal.enums.ProcessType;
import com.sorted.portal.enums.ResponseCode;
import com.sorted.portal.exceptions.CustomIllegalArgumentsException;
import com.sorted.portal.helper.AggregationFilter.SEFilter;
import com.sorted.portal.helper.AggregationFilter.SEFilterType;
import com.sorted.portal.helper.AggregationFilter.WhereClause;
import com.sorted.portal.helper.SERequest;
import com.sorted.portal.helper.SEResponse;
import com.sorted.portal.otp_service.ManageOtp;
import com.sorted.portal.utils.SERegExpUtils;

@RestController
@RequestMapping("/auth")
public class ManageLogin_BLService {

	@Autowired
	private Users_Service users_Service;

	@Autowired
	private RoleService roleService;

	@Autowired
	private ManageOtp manageOtp;

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@PostMapping("/login")
	public SEResponse login(@RequestBody SERequest request) {
		LoginReqBean req = request.getGenericRequestDataObject(LoginReqBean.class);
		if (!StringUtils.hasText(req.getMobile_no())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_MOBILE);
		}
		if (!StringUtils.hasText(req.getPassword())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_PASS);
		}
		SEFilter filterU = new SEFilter(SEFilterType.AND);
		filterU.addClause(WhereClause.eq(Users.Fields.mobile_no, req.getMobile_no()));
		filterU.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		List<Users> listU = users_Service.repoFind(filterU);
		if (CollectionUtils.isEmpty(listU) || listU.size() > 1) {
			throw new CustomIllegalArgumentsException(ResponseCode.LOGIN_FAILED);
		}
		Users users = listU.get(0);
		String password = users.getPassword();
		if (!passwordEncoder.matches(req.getPassword(), password)) {
			throw new CustomIllegalArgumentsException(ResponseCode.LOGIN_FAILED);
		}
		if (users.getStatus() != User_Status.ACTIVE.getId()) {
			throw new CustomIllegalArgumentsException(ResponseCode.USER_BLOCKED);
		}

		SEFilter filterR = new SEFilter(SEFilterType.AND);
		filterR.addClause(WhereClause.eq(BaseMongoEntity.Fields.id, users.getRole_id()));
		filterR.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		Role role = roleService.repoFindOne(filterR);
		if (role == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.ROLE_MISSING);
		}
		manageOtp.send(req.getMobile_no(), users.getId(), ProcessType.LOGIN);
		return SEResponse.getBasicSuccessResponseObject(users.getId(), ResponseCode.SUCCESSFUL);
	}

	@PostMapping("/verify/otp")
	public SEResponse verifyOtp(@RequestBody SERequest request) {
		VerifyOtpReqBean req = request.getGenericRequestDataObject(VerifyOtpReqBean.class);
		if (!StringUtils.hasText(req.getOtp())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_OTP);
		}
		if (!SERegExpUtils.isOtp(req.getOtp())) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_OTP);
		}
		if (!StringUtils.hasText(req.getUser_id())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_USER_ID);
		}
		SEFilter filterU = new SEFilter(SEFilterType.AND);
		filterU.addClause(WhereClause.eq(BaseMongoEntity.Fields.id, req.getUser_id()));
		filterU.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		Users users = users_Service.repoFindOne(filterU);
		if (users == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.USER_NOT_FOUND);
		}

		manageOtp.verify(users.getMobile_no(), users.getId(), req.getOtp(), ProcessType.LOGIN, Defaults.SIGN_IN);
		SEFilter filterR = new SEFilter(SEFilterType.AND);
		filterR.addClause(WhereClause.eq(BaseMongoEntity.Fields.id, users.getRole_id()));
		filterR.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		Role role = roleService.repoFindOne(filterR);
		if (role == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.ROLE_MISSING);
		}

		Gson gson = new Gson();
		UsersBean usersBean = gson.fromJson(gson.toJson(users), UsersBean.class);
		usersBean.setPassword("");
		usersBean.setRole(role);

		return SEResponse.getBasicSuccessResponseObject(usersBean, ResponseCode.SUCCESSFUL);

	}

}
