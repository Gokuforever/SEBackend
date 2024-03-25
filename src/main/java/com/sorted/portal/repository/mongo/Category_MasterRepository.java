package com.sorted.portal.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.portal.entity.mongo.Category_Master;
import com.sorted.portal.helper.BaseMongoRepository;

@Repository
public interface Category_MasterRepository extends BaseMongoRepository<String, Category_Master> {

	@Override
	default Class<Category_Master> getEntityType() {
		return Category_Master.class;
	}

}
