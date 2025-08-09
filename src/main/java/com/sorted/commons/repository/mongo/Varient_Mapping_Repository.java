package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Varient_Mapping;
import com.sorted.commons.helper.BaseMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Varient_Mapping_Repository extends BaseMongoRepository<String, Varient_Mapping> {

	@Override
	default Class<Varient_Mapping> getEntityType() {
		return Varient_Mapping.class;
	}

}
