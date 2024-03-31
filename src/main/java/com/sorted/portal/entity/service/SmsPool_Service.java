package com.sorted.portal.entity.service;

import org.springframework.stereotype.Service;

import com.sorted.portal.entity.mongo.SmsPool;
import com.sorted.portal.repository.mongo.SmsPool_Repository;

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
