package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.SmsPool;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface SmsPool_Repository extends BaseMongoRepository<String, SmsPool> {

	@Override
	default Class<SmsPool> getEntityType() {
		return SmsPool.class;
	}

}
