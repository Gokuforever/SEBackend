package com.sorted.portal.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.portal.entity.mongo.Catagory_Master;
import com.sorted.portal.helper.BaseMongoRepository;

@Repository
public interface Catagory_MasterRepository extends BaseMongoRepository<String, Catagory_Master> {

	@Override
	default Class<Catagory_Master> getEntityType() {
		return Catagory_Master.class;
	}

}
