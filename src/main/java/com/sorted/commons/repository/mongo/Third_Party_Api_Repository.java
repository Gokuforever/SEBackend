package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Third_Party_Api;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface Third_Party_Api_Repository extends BaseMongoRepository<String, Third_Party_Api>{

	@Override
	default Class<Third_Party_Api> getEntityType() {
		return Third_Party_Api.class;
	}
}
