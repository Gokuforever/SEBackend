package com.sorted.portal.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.portal.entity.mongo.Otp;
import com.sorted.portal.helper.BaseMongoRepository;

@Repository
public interface Otp_Repository extends BaseMongoRepository<String, Otp> {

	@Override
	default Class<Otp> getEntityType() {
		return Otp.class;
	}
}
