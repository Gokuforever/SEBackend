package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Customer_Leads;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface Customer_Leads_Repository extends BaseMongoRepository<String, Customer_Leads> {

	@Override
	default Class<Customer_Leads> getEntityType() {
		return Customer_Leads.class;
	}

}
