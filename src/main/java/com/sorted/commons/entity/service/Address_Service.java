package com.sorted.commons.entity.service;

import org.springframework.stereotype.Service;

import com.sorted.commons.entity.mongo.Address;
import com.sorted.commons.repository.mongo.Address_Repository;
import com.sorted.commons.utils.CommonUtils;

@Service
public class Address_Service extends GenericEntityServiceImpl<String, Address, Address_Repository> {

	@Override
	protected Class<Address_Repository> getRepoClass() {
		return Address_Repository.class;
	}

	@Override
	protected void validateBeforeCreate(Address inE) throws RuntimeException {
		String code = CommonUtils.createCode("ADD");
		inE.setCode(code);
	}

	@Override
	protected void validateBeforeUpdate(String id, Address inE) throws RuntimeException {
	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {
	}

}
