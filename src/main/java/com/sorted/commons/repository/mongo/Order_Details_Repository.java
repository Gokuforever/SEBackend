package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Order_Details;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface Order_Details_Repository extends BaseMongoRepository<String, Order_Details>{

	@Override
	default Class<Order_Details> getEntityType() {
		return Order_Details.class;
	}
}
