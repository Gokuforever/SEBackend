package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Role;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface RoleRepository extends BaseMongoRepository<String, Role>{

	@Override
	default Class<Role> getEntityType() {
		return Role.class;
	}
}
