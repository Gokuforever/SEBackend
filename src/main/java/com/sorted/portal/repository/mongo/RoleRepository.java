package com.sorted.portal.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.portal.entity.mongo.Role;
import com.sorted.portal.helper.BaseMongoRepository;

@Repository
public interface RoleRepository extends BaseMongoRepository<String, Role>{

	@Override
	default Class<Role> getEntityType() {
		return Role.class;
	}
}
