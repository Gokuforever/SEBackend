package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.Product_Master;
import com.sorted.commons.repository.mongo.Product_Master_Repository;
import org.springframework.stereotype.Service;

@Service
public class Product_Master_Service extends GenericEntityServiceImpl<String, Product_Master, Product_Master_Repository>{

	@Override
	protected Class<Product_Master_Repository> getRepoClass() {
		return Product_Master_Repository.class;
	}

	@Override
	protected void validateBeforeCreate(Product_Master inE) throws RuntimeException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void validateBeforeUpdate(String id, Product_Master inE) throws RuntimeException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {
		// TODO Auto-generated method stub
		
	}

}
