package com.sorted.commons.entity.service;

import org.springframework.stereotype.Service;

import com.sorted.commons.entity.mongo.Users;
import com.sorted.commons.enums.All_Status.User_Status;
import com.sorted.commons.repository.mongo.Users_Repository;

@Service
public class Users_Service extends GenericEntityServiceImpl<String, Users, Users_Repository> {

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
}
