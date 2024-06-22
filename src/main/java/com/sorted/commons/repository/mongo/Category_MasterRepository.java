package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Category_Master;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface Category_MasterRepository extends BaseMongoRepository<String, Category_Master> {

	@Override
	default Class<Category_Master> getEntityType() {
		return Category_Master.class;
	}

}
