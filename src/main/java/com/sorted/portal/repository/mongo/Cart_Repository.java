package com.sorted.portal.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.portal.entity.mongo.Cart;
import com.sorted.portal.helper.BaseMongoRepository;

@Repository
public interface Cart_Repository extends BaseMongoRepository<String, Cart> {

	@Override
	default Class<Cart> getEntityType() {
		return Cart.class;
	}
}
