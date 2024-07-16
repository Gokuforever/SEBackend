package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Varient_Mapping;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface Varient_Mapping_Repository extends BaseMongoRepository<String, Varient_Mapping> {

	@Override
	default Class<Varient_Mapping> getEntityType() {
		return Varient_Mapping.class;
	}

}
