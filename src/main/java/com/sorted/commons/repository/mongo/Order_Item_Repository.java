package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Order_Item;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface Order_Item_Repository extends BaseMongoRepository<String, Order_Item> {

	@Override
	default Class<Order_Item> getEntityType() {
		return Order_Item.class;
	}
}
