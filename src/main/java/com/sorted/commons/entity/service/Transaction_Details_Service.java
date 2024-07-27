package com.sorted.commons.entity.service;

import org.springframework.stereotype.Service;

import com.sorted.commons.entity.mongo.Transaction_Details;
import com.sorted.commons.repository.mongo.Transaction_Details_Repository;

@Service
public class Transaction_Details_Service
		extends GenericEntityServiceImpl<String, Transaction_Details, Transaction_Details_Repository> {

	@Override
	protected Class<Transaction_Details_Repository> getRepoClass() {
		return Transaction_Details_Repository.class;
	}

	@Override
	protected void validateBeforeCreate(Transaction_Details inE) throws RuntimeException {
	}

	@Override
	protected void validateBeforeUpdate(String id, Transaction_Details inE) throws RuntimeException {
	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {
	}

}
