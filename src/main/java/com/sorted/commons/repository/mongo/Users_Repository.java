package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Users;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface Users_Repository extends BaseMongoRepository<String, Users> {

	@Override
	default Class<Users> getEntityType() {
		return Users.class;
	}
}
