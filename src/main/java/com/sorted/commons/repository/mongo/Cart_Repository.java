package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Cart;
import com.sorted.commons.helper.BaseMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Cart_Repository extends BaseMongoRepository<String, Cart> {

	@Override
	default Class<Cart> getEntityType() {
		return Cart.class;
	}
}
