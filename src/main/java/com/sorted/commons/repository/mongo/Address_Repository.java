package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Address;
import com.sorted.commons.helper.BaseMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Address_Repository extends BaseMongoRepository<String, Address> {

	@Override
	default Class<Address> getEntityType() {
		return Address.class;
	}
}
