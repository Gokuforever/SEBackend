package com.sorted.commons.entity.service;

import org.springframework.stereotype.Service;

import com.sorted.commons.entity.mongo.Cart;
import com.sorted.commons.repository.mongo.Cart_Repository;

@Service
public class Cart_Service extends GenericEntityServiceImpl<String, Cart, Cart_Repository> {

	@Override
	protected Class<Cart_Repository> getRepoClass() {
		return Cart_Repository.class;
	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {

	}

	@Override
	protected void validateBeforeCreate(Cart inE) throws RuntimeException {

	}

	@Override
	protected void validateBeforeUpdate(String id, Cart inE) throws RuntimeException {

	}

}
