package com.sorted.portal.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.portal.entity.mongo.Products;
import com.sorted.portal.helper.BaseMongoRepository;

@Repository
public interface ProductRepository extends BaseMongoRepository<String, Products> {

	@Override
	default Class<Products> getEntityType() {
		return Products.class;
	}
}
