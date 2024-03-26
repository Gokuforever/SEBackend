package com.sorted.portal.entity.service;

import org.springframework.stereotype.Service;

import com.sorted.portal.entity.mongo.Otp;
import com.sorted.portal.repository.mongo.Otp_Repository;

@Service
public class Otp_Service extends GenericEntityServiceImpl<String, Otp, Otp_Repository> {

	@Override
	protected Class<Otp_Repository> getRepoClass() {
		return Otp_Repository.class;
	}

	@Override
	protected void validateBeforeCreate(Otp inE) throws RuntimeException {

	}

	@Override
	protected void validateBeforeUpdate(String id, Otp inE) throws RuntimeException {

	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {

	}

}
