package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Role;
import com.sorted.commons.helper.BaseMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseMongoRepository<String, Role>{

	@Override
	default Class<Role> getEntityType() {
		return Role.class;
	}
}
