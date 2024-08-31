package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Product_Master;
import com.sorted.commons.helper.BaseMongoRepository;

public interface Product_Master_Repository extends BaseMongoRepository<String, Product_Master> {

	@Override
	default Class<Product_Master> getEntityType() {
		return Product_Master.class;
	}
}
