package com.sorted.portal.entity.service;

import org.springframework.stereotype.Service;

import com.sorted.portal.entity.mongo.Customer_Leads;
import com.sorted.portal.repository.mongo.Customer_Leads_Repository;

@Service
public class Customer_Leads_Service
		extends GenericEntityServiceImpl<String, Customer_Leads, Customer_Leads_Repository> {

	@Override
	protected Class<Customer_Leads_Repository> getRepoClass() {
		return Customer_Leads_Repository.class;
	}

	@Override
	protected void validateBeforeCreate(Customer_Leads inE) throws RuntimeException {

	}

	@Override
	protected void validateBeforeUpdate(String id, Customer_Leads inE) throws RuntimeException {

	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {

	}

}
