package com.sorted.portal.entity.service;

import java.time.Year;

import org.springframework.stereotype.Service;

import com.sorted.portal.entity.mongo.Products;
import com.sorted.portal.repository.mongo.ProductRepository;
import com.sorted.portal.utils.CommonUtils;

@Service
public class ProductService extends GenericEntityServiceImpl<String, Products, ProductRepository> {

	@Override
	protected Class<ProductRepository> getRepoClass() {
		return ProductRepository.class;
	}

	@Override
	protected void validateBeforeCreate(Products inE) throws RuntimeException {
		long nanoseconds = CommonUtils.getNanoseconds();
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("PID-");
		stringBuffer.append(nanoseconds);
		stringBuffer.append("-");
		stringBuffer.append(Year.now());

		inE.setProduct_code(stringBuffer.toString());
	}

	@Override
	protected void validateBeforeUpdate(String id, Products inE) throws RuntimeException {
	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {

	}

}
