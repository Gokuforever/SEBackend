package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Products;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface ProductRepository extends BaseMongoRepository<String, Products> {

	@Override
	default Class<Products> getEntityType() {
		return Products.class;
	}
}
