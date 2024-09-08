package com.sorted.commons.entity.service;

import org.springframework.stereotype.Service;

import com.sorted.commons.entity.mongo.Order_Item;
import com.sorted.commons.repository.mongo.Order_Item_Repository;

@Service
public class Order_Item_Service extends GenericEntityServiceImpl<String, Order_Item, Order_Item_Repository> {

	@Override
	protected Class<Order_Item_Repository> getRepoClass() {
		return Order_Item_Repository.class;
	}

	@Override
	protected void validateBeforeCreate(Order_Item inE) throws RuntimeException {
	}

	@Override
	protected void validateBeforeUpdate(String id, Order_Item inE) throws RuntimeException {
	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {
	}

}
