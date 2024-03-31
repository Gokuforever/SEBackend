package com.sorted.portal.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.portal.entity.mongo.SmsPool;
import com.sorted.portal.helper.BaseMongoRepository;

@Repository
public interface SmsPool_Repository extends BaseMongoRepository<String, SmsPool> {

	@Override
	default Class<SmsPool> getEntityType() {
		return SmsPool.class;
	}

}
