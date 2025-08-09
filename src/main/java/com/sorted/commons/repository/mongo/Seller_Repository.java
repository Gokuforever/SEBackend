package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Seller;
import com.sorted.commons.helper.BaseMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Seller_Repository extends BaseMongoRepository<String, Seller> {

	@Override
	default Class<Seller> getEntityType() {
		return Seller.class;
	}
}
