package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Address;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface Address_Repository extends BaseMongoRepository<String, Address> {

	@Override
	default Class<Address> getEntityType() {
		return Address.class;
	}
}
