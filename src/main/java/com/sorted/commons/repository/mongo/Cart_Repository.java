package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Cart;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface Cart_Repository extends BaseMongoRepository<String, Cart> {

	@Override
	default Class<Cart> getEntityType() {
		return Cart.class;
	}
}
