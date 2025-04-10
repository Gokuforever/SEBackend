package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.User_Auth_Details;
import com.sorted.commons.helper.BaseMongoRepository;

public interface User_Auth_Details_Repository extends BaseMongoRepository<String, User_Auth_Details> {

	@Override
	default Class<User_Auth_Details> getEntityType() {
		return User_Auth_Details.class;
	}
}
