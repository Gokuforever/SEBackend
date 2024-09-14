package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.File_Upload_Details;
import com.sorted.commons.helper.BaseMongoRepository;

public interface File_Upload_Details_Repository extends BaseMongoRepository<String, File_Upload_Details> {

	@Override
	default Class<File_Upload_Details> getEntityType() {
		return File_Upload_Details.class;
	}
}
