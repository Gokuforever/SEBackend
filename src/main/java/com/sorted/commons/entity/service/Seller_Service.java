package com.sorted.commons.entity.service;

import org.springframework.stereotype.Service;

import com.sorted.commons.entity.mongo.Seller;
import com.sorted.commons.repository.mongo.Seller_Repository;

@Service
public class Seller_Service extends GenericEntityServiceImpl<String, Seller, Seller_Repository> {

	@Override
	protected Class<Seller_Repository> getRepoClass() {
		return Seller_Repository.class;
	}

	@Override
	protected void validateBeforeCreate(Seller inE) throws RuntimeException {
	}

	@Override
	protected void validateBeforeUpdate(String id, Seller inE) throws RuntimeException {
	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {
	}

}
