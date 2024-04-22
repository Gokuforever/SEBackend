package com.sorted.portal.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.portal.entity.mongo.Third_Party_Api;
import com.sorted.portal.helper.BaseMongoRepository;

@Repository
public interface Third_Party_Api_Repository extends BaseMongoRepository<String, Third_Party_Api>{

	@Override
	default Class<Third_Party_Api> getEntityType() {
		return Third_Party_Api.class;
	}
}
