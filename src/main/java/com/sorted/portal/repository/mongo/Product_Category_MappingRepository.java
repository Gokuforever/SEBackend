package com.sorted.portal.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.portal.entity.mongo.Product_Category_Mapping;
import com.sorted.portal.helper.BaseMongoRepository;

@Repository
public interface Product_Category_MappingRepository extends BaseMongoRepository<String, Product_Category_Mapping> {

	@Override
	default Class<Product_Category_Mapping> getEntityType() {
		return Product_Category_Mapping.class;
	}
}
