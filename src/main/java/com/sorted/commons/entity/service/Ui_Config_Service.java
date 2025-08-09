package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.Ui_Config;
import com.sorted.commons.repository.mongo.Ui_Config_Repository;
import org.springframework.stereotype.Service;

@Service
public class Ui_Config_Service extends GenericEntityServiceImpl<String, Ui_Config, Ui_Config_Repository> {

	@Override
	protected Class<Ui_Config_Repository> getRepoClass() {
		return Ui_Config_Repository.class;
	}

	@Override
	protected void validateBeforeCreate(Ui_Config inE) throws RuntimeException {

	}

	@Override
	protected void validateBeforeUpdate(String id, Ui_Config inE) throws RuntimeException {

	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {

	}

}
