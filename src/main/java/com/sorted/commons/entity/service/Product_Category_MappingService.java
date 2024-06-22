package com.sorted.commons.entity.service;

import org.springframework.stereotype.Service;

import com.sorted.commons.entity.mongo.Product_Category_Mapping;
import com.sorted.commons.repository.mongo.Product_Category_MappingRepository;

@Service
public class Product_Category_MappingService
		extends GenericEntityServiceImpl<String, Product_Category_Mapping, Product_Category_MappingRepository> {

	@Override
	protected Class<Product_Category_MappingRepository> getRepoClass() {
		return Product_Category_MappingRepository.class;
	}

	@Override
	protected void validateBeforeCreate(Product_Category_Mapping inE) throws RuntimeException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validateBeforeUpdate(String id, Product_Category_Mapping inE) throws RuntimeException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {
		// TODO Auto-generated method stub

	}

}
