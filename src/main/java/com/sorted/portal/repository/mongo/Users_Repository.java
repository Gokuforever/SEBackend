package com.sorted.portal.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.portal.entity.mongo.Users;
import com.sorted.portal.helper.BaseMongoRepository;

@Repository
public interface Users_Repository extends BaseMongoRepository<String, Users> {

	@Override
	default Class<Users> getEntityType() {
		return Users.class;
	}
}
