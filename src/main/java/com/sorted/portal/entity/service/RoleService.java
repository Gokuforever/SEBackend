package com.sorted.portal.entity.service;

import org.springframework.stereotype.Service;

import com.sorted.portal.entity.mongo.Role;
import com.sorted.portal.repository.mongo.RoleRepository;

@Service
public class RoleService extends GenericEntityServiceImpl<String, Role, RoleRepository> {

	@Override
	protected Class<RoleRepository> getRepoClass() {
		return RoleRepository.class;
	}

	@Override
	protected void validateBeforeCreate(Role inE) throws RuntimeException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validateBeforeUpdate(String id, Role inE) throws RuntimeException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {
		// TODO Auto-generated method stub

	}

}
