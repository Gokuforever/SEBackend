package com.sorted.commons.entity.service;

import org.springframework.stereotype.Service;

import com.sorted.commons.entity.mongo.Third_Party_Api;
import com.sorted.commons.repository.mongo.Third_Party_Api_Repository;

@Service
public class Third_Party_Api_Service
		extends GenericEntityServiceImpl<String, Third_Party_Api, Third_Party_Api_Repository> {

	@Override
	protected Class<Third_Party_Api_Repository> getRepoClass() {
		return Third_Party_Api_Repository.class;
	}

	@Override
	protected void validateBeforeCreate(Third_Party_Api inE) throws RuntimeException {
	}

	@Override
	protected void validateBeforeUpdate(String id, Third_Party_Api inE) throws RuntimeException {
	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {
	}

}
