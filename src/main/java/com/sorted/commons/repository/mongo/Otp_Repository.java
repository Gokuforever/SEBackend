package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Otp;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface Otp_Repository extends BaseMongoRepository<String, Otp> {

	@Override
	default Class<Otp> getEntityType() {
		return Otp.class;
	}
}
