package com.sorted.commons.entity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.sorted.commons.beans.OTPResponse;
import com.sorted.commons.beans.UsersBean;
import com.sorted.commons.constants.Defaults;
import com.sorted.commons.entity.mongo.BaseMongoEntity;
import com.sorted.commons.entity.mongo.Role;
import com.sorted.commons.entity.mongo.Seller;
import com.sorted.commons.entity.mongo.Users;
import com.sorted.commons.enums.Activity;
import com.sorted.commons.enums.All_Status.User_Status;
import com.sorted.commons.enums.EntityDetails;
import com.sorted.commons.enums.Permission;
import com.sorted.commons.enums.ProcessType;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import com.sorted.commons.helper.AggregationFilter.SEFilter;
import com.sorted.commons.helper.AggregationFilter.SEFilterType;
import com.sorted.commons.helper.AggregationFilter.WhereClause;
import com.sorted.commons.manage.otp.ManageOtp;
import com.sorted.commons.repository.mongo.Users_Repository;
import com.sorted.commons.utils.GsonUtils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Users_Service extends GenericEntityServiceImpl<String, Users, Users_Repository> {

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	private ManageOtp manageOtp;

	@Autowired
	private RoleService roleService;

	@Autowired
	private Seller_Service seller_Service;

	@Override
	protected Class<Users_Repository> getRepoClass() {
		return Users_Repository.class;
	}

	@Override
	protected void validateBeforeCreate(Users inE) throws RuntimeException {
		inE.setStatus(User_Status.ACTIVE.getId());
	}

	@Override
	protected void validateBeforeUpdate(String id, Users inE) throws RuntimeException {

	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {

	}

	public OTPResponse validateUserForLogin(@NonNull String mobile_no, @NonNull String password) {
		SEFilter filterU = new SEFilter(SEFilterType.AND);
		filterU.addClause(WhereClause.eq(Users.Fields.mobile_no, mobile_no));
		filterU.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		Users user = this.repoFindOne(filterU);
		if (user == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.LOGIN_FAILED);
		}
		String pass = user.getPassword();
		if (!passwordEncoder.matches(password, pass)) {
			throw new CustomIllegalArgumentsException(ResponseCode.LOGIN_FAILED);
		}
		if (user.getStatus() != User_Status.ACTIVE.getId() || !Boolean.TRUE.equals(user.getIs_verified())) {
			throw new CustomIllegalArgumentsException(ResponseCode.USER_BLOCKED);
		}

		SEFilter filterR = new SEFilter(SEFilterType.AND);
		filterR.addClause(WhereClause.eq(BaseMongoEntity.Fields.id, user.getRole_id()));
		filterR.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		Role role = roleService.repoFindOne(filterR);
		if (role == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.ROLE_MISSING);
		}
		String uuid = manageOtp.send(mobile_no, user.getId(), ProcessType.SIGN_IN, EntityDetails.USERS,
				Defaults.SIGN_IN);
		OTPResponse response = new OTPResponse();
		response.setReference_id(uuid);
		response.setProcess_type(ProcessType.SIGN_IN.name());
		response.setEntity_id(user.getId());
		return response;
	}

	public UsersBean validateAndGetUserInfo(String req_user_id) {
		return this.validateAndGetUserInfo(req_user_id, null);
	}

	public UsersBean validateAndGetUserInfo(@NonNull String req_user_id, String req_role_id) {
		try {
			log.info("validateUserForLogin started.");
			SEFilter filterU = new SEFilter(SEFilterType.AND);
			filterU.addClause(WhereClause.eq(BaseMongoEntity.Fields.id, req_user_id));
			if (StringUtils.hasText(req_role_id)) {
				filterU.addClause(WhereClause.eq(Users.Fields.role_id, req_role_id));
			}
			filterU.addClause(WhereClause.eq(Users.Fields.is_verified, true));
			filterU.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

			Users users = this.repoFindOne(filterU);
			if (users == null) {
				throw new CustomIllegalArgumentsException(ResponseCode.USER_NOT_FOUND);
			}
			if (!StringUtils.hasText(req_role_id)) {
				req_role_id = users.getRole_id();
			}
			SEFilter filterR = new SEFilter(SEFilterType.AND);
			filterR.addClause(WhereClause.eq(BaseMongoEntity.Fields.id, req_role_id));
			filterR.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

			Role role = roleService.repoFindOne(filterR);
			if (role == null) {
				throw new CustomIllegalArgumentsException(ResponseCode.ROLE_MISSING);
			}

			Gson gson = GsonUtils.getGson();
			UsersBean usersBean = gson.fromJson(gson.toJson(users), UsersBean.class);
			usersBean.setPassword("");
			usersBean.setRole(role);
			this.validateHierarchy(role, usersBean);
			log.info("validateUserForLogin ended.");
			return usersBean;
		} catch (CustomIllegalArgumentsException ex) {
			throw ex;
		} catch (Exception e) {
			log.error("validateUserForLogin:: error occerred:: {}", e.getMessage());
			throw new CustomIllegalArgumentsException(ResponseCode.ERR_0001);
		}
	}

	public UsersBean validateUserForActivity(@NonNull String req_user_id, @NonNull String req_role_id,
			@NonNull Activity activity, @NonNull Permission permission) {
		log.info("validateUserForActivity started.");
		SEFilter filterU = new SEFilter(SEFilterType.AND);
		filterU.addClause(WhereClause.eq(BaseMongoEntity.Fields.id, req_user_id));
		filterU.addClause(WhereClause.eq(Users.Fields.role_id, req_role_id));
		filterU.addClause(WhereClause.eq(Users.Fields.is_verified, true));
		filterU.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		Users users = this.repoFindOne(filterU);
		if (users == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.USER_NOT_FOUND);
		}
		SEFilter filterR = new SEFilter(SEFilterType.AND);
		filterR.addClause(WhereClause.eq(BaseMongoEntity.Fields.id, req_role_id));
		filterR.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		Role role = roleService.repoFindOne(filterR);
		if (role == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.ROLE_MISSING);
		}
		if (CollectionUtils.isEmpty(role.getRole_permissions())) {
			throw new CustomIllegalArgumentsException(ResponseCode.ACCESS_DENIED);
		}
		boolean hasAccess = role.getRole_permissions().stream().anyMatch(
				e -> (e.getActivity_id() == activity.getId() && e.getPermissions().contains(permission.getId())));
		if (!hasAccess) {
			throw new CustomIllegalArgumentsException(ResponseCode.ACCESS_DENIED);
		}
		Gson gson = GsonUtils.getGson();
		UsersBean usersBean = gson.fromJson(gson.toJson(users), UsersBean.class);
		usersBean.setPassword("");
		usersBean.setRole(role);

		this.validateHierarchy(role, usersBean);
		log.info("validateUserForActivity ended.");
		return usersBean;
	}

	private void validateHierarchy(Role role, UsersBean usersBean) {
		switch (role.getUser_type()) {
		case SELLER:
			if (!StringUtils.hasText(role.getSe_id())) {
				throw new CustomIllegalArgumentsException(ResponseCode.ACCESS_DENIED);
			}
			SEFilter filterS = new SEFilter(SEFilterType.AND);
			filterS.addClause(WhereClause.eq(BaseMongoEntity.Fields.id, role.getSe_id()));
			filterS.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

			Seller seller = seller_Service.repoFindOne(filterS);
			if (seller == null) {
				throw new CustomIllegalArgumentsException(ResponseCode.ACCESS_DENIED);
			}
			usersBean.setSeller(seller);
			break;
		default:
			break;
		}
	}
}
