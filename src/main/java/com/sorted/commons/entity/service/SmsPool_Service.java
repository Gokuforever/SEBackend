package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.SmsPool;
import com.sorted.commons.repository.mongo.SmsPool_Repository;
import org.springframework.stereotype.Service;

@Service
public class SmsPool_Service extends GenericEntityServiceImpl<String, SmsPool, SmsPool_Repository> {

	@Override
	protected Class<SmsPool_Repository> getRepoClass() {
		return SmsPool_Repository.class;
	}

	@Override
	protected void validateBeforeCreate(SmsPool inE) throws RuntimeException {

	}

	@Override
	protected void validateBeforeUpdate(String id, SmsPool inE) throws RuntimeException {

	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {

	}

}
