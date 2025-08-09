package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Order_Details;
import com.sorted.commons.helper.BaseMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Order_Details_Repository extends BaseMongoRepository<String, Order_Details>{

	@Override
	default Class<Order_Details> getEntityType() {
		return Order_Details.class;
	}
}
