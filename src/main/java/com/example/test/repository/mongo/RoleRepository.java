package com.example.test.repository.mongo;

import org.springframework.stereotype.Repository;

import com.example.test.entity.mongo.Role;
import com.example.test.helper.BaseMongoRepository;

@Repository
public interface RoleRepository extends BaseMongoRepository<String, Role>{

	@Override
	default Class<Role> getEntityType() {
		return Role.class;
	}
}
