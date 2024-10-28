package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Search_History;
import com.sorted.commons.helper.BaseMongoRepository;

public interface Search_History_Repository extends BaseMongoRepository<String, Search_History> {

	@Override
	default Class<Search_History> getEntityType() {
		return Search_History.class;
	}
}
