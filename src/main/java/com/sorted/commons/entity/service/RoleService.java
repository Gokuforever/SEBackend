package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.Role;
import com.sorted.commons.repository.mongo.RoleRepository;
import org.springframework.stereotype.Service;

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
