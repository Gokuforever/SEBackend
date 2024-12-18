package com.sorted.commons.repository.mongo;

import org.springframework.stereotype.Repository;

import com.sorted.commons.entity.mongo.Ui_Config;
import com.sorted.commons.helper.BaseMongoRepository;

@Repository
public interface Ui_Config_Repository extends BaseMongoRepository<String, Ui_Config> {

	@Override
	default Class<Ui_Config> getEntityType() {
		return Ui_Config.class;
	}
}
