package com.sorted.portal.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.portal.entity.mongo.Product_Catagory_Mapping;
import com.sorted.portal.helper.BaseMongoRepository;

@Repository
public interface Product_Catagory_MappingRepository extends BaseMongoRepository<String, Product_Catagory_Mapping> {

	@Override
	default Class<Product_Catagory_Mapping> getEntityType() {
		return Product_Catagory_Mapping.class;
	}
}
