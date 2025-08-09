package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Otp;
import com.sorted.commons.helper.BaseMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Otp_Repository extends BaseMongoRepository<String, Otp> {

	@Override
	default Class<Otp> getEntityType() {
		return Otp.class;
	}
}
