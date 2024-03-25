package com.sorted.portal.entity.service;

import org.springframework.stereotype.Service;

import com.sorted.portal.entity.mongo.Product_Catagory_Mapping;
import com.sorted.portal.repository.mongo.Product_Catagory_MappingRepository;

@Service
public class Product_Catagory_MappingService
		extends GenericEntityServiceImpl<String, Product_Catagory_Mapping, Product_Catagory_MappingRepository> {

	@Override
	protected Class<Product_Catagory_MappingRepository> getRepoClass() {
		return Product_Catagory_MappingRepository.class;
	}

	@Override
	protected void validateBeforeCreate(Product_Catagory_Mapping inE) throws RuntimeException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validateBeforeUpdate(String id, Product_Catagory_Mapping inE) throws RuntimeException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {
		// TODO Auto-generated method stub

	}

}
