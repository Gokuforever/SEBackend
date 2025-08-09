package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Category_Master;
import com.sorted.commons.helper.BaseMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Category_MasterRepository extends BaseMongoRepository<String, Category_Master> {

	@Override
	default Class<Category_Master> getEntityType() {
		return Category_Master.class;
	}

}
