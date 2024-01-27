package com.example.test.entity.service;

import org.springframework.stereotype.Service;

import com.example.test.entity.mongo.Role;
import com.example.test.repository.mongo.RoleRepository;

@Service
public class RoleService extends GenericEntityServiceImpl<String, Role, RoleRepository> {

	@Override
	protected Class<RoleRepository> getRepoClass() {
		return RoleRepository.class;
	}

}
