package com.sorted.portal.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.portal.entity.mongo.Order_Details;
import com.sorted.portal.helper.BaseMongoRepository;

@Repository
public interface Order_Details_Repository extends BaseMongoRepository<String, Order_Details>{

	@Override
	default Class<Order_Details> getEntityType() {
		return Order_Details.class;
	}
}
