package com.sorted.commons.entity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.sorted.commons.beans.UsersBean;
import com.sorted.commons.entity.mongo.BaseMongoEntity;
import com.sorted.commons.entity.mongo.Role;
import com.sorted.commons.entity.mongo.Users;
import com.sorted.commons.enums.All_Status.User_Status;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import com.sorted.commons.helper.AggregationFilter.SEFilter;
import com.sorted.commons.helper.AggregationFilter.SEFilterType;
import com.sorted.commons.helper.AggregationFilter.WhereClause;
import com.sorted.commons.repository.mongo.Users_Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Users_Service extends GenericEntityServiceImpl<String, Users, Users_Repository> {

//	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	private RoleService roleService;

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

	public UsersBean validateUserForLogin(String req_user_id, String req_role_id) {
		try {
			log.info("validateUserForLogin started.");
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
			
			
			Gson gson = new Gson();
			UsersBean usersBean = gson.fromJson(gson.toJson(users), UsersBean.class);
			usersBean.setPassword("");
			usersBean.setRole(role);
			log.info("validateUserForLogin ended.");
			return usersBean;
		} catch (Exception e) {
			log.error("validateUserForLogin:: error occerred:: {}", e.getMessage());
			throw new CustomIllegalArgumentsException(ResponseCode.ERR_0001);
		}
	}
}
