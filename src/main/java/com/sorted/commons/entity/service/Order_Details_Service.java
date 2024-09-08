package com.sorted.commons.entity.service;

import java.time.LocalDate;
import java.time.Year;

import org.springframework.stereotype.Service;

import com.sorted.commons.entity.mongo.Order_Details;
import com.sorted.commons.repository.mongo.Order_Details_Repository;
import com.sorted.commons.utils.CommonUtils;

@Service
public class Order_Details_Service extends GenericEntityServiceImpl<String, Order_Details, Order_Details_Repository>{

	@Override
	protected Class<Order_Details_Repository> getRepoClass() {
		return Order_Details_Repository.class;
	}

	@Override
	protected void validateBeforeCreate(Order_Details inE) throws RuntimeException {
		long nanoseconds = CommonUtils.getNanoseconds();
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("ORD-");
		stringBuffer.append(LocalDate.now().getMonth());
		stringBuffer.append(Year.now());
		stringBuffer.append(nanoseconds);

		inE.setCode(stringBuffer.toString());
	}

	@Override
	protected void validateBeforeUpdate(String id, Order_Details inE) throws RuntimeException {
	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {
	}

}
