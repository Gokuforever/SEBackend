package com.sorted.commons.entity.service;

import org.springframework.stereotype.Service;

import com.sorted.commons.entity.mongo.Transaction_Req_Response;
import com.sorted.commons.repository.mongo.Transaction_Req_Response_Repository;

@Service
public class Transaction_Req_Response_Service
		extends GenericEntityServiceImpl<String, Transaction_Req_Response, Transaction_Req_Response_Repository> {

	@Override
	protected Class<Transaction_Req_Response_Repository> getRepoClass() {
		return Transaction_Req_Response_Repository.class;
	}

	@Override
	protected void validateBeforeCreate(Transaction_Req_Response inE) throws RuntimeException {
	}

	@Override
	protected void validateBeforeUpdate(String id, Transaction_Req_Response inE) throws RuntimeException {
	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {
	}

}
