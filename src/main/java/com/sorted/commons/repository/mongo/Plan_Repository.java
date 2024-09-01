package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Plans;
import com.sorted.commons.helper.BaseMongoRepository;

public interface Plan_Repository extends BaseMongoRepository<String, Plans> {

	@Override
	default Class<Plans> getEntityType() {
		return Plans.class;
	}
}
