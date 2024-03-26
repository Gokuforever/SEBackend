package com.sorted.portal.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.portal.entity.mongo.Customer_Leads;
import com.sorted.portal.helper.BaseMongoRepository;

@Repository
public interface Customer_Leads_Repository extends BaseMongoRepository<String, Customer_Leads> {

	@Override
	default Class<Customer_Leads> getEntityType() {
		return Customer_Leads.class;
	}

}
