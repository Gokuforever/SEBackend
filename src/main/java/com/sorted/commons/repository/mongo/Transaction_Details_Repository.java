package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Transaction_Details;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface Transaction_Details_Repository extends BaseMongoRepository<String, Transaction_Details> {

	@Override
	default Class<Transaction_Details> getEntityType() {
		return Transaction_Details.class;
	}
}
