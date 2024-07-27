package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Seller;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface Seller_Repository extends BaseMongoRepository<String, Seller> {

	@Override
	default Class<Seller> getEntityType() {
		return Seller.class;
	}
}
