package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.SmsPool;
import com.sorted.commons.helper.BaseMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsPool_Repository extends BaseMongoRepository<String, SmsPool> {

	@Override
	default Class<SmsPool> getEntityType() {
		return SmsPool.class;
	}

}
