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

}
