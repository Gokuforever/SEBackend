package com.sorted.portal.entity.service;

import java.time.Year;

import org.springframework.stereotype.Service;

import com.sorted.portal.entity.mongo.Category_Master;
import com.sorted.portal.repository.mongo.Category_MasterRepository;
import com.sorted.portal.utils.CommonUtils;

@Service
public class Category_MasterService
		extends GenericEntityServiceImpl<String, Category_Master, Category_MasterRepository> {

	@Override
	protected Class<Category_MasterRepository> getRepoClass() {
		return Category_MasterRepository.class;
	}

	@Override
	protected void validateBeforeCreate(Category_Master inE) throws RuntimeException {
		long nanoseconds = CommonUtils.getNanoseconds();
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("CI-");
		stringBuffer.append(nanoseconds);
		stringBuffer.append("-");
		stringBuffer.append(Year.now());

		inE.setCategory_code(stringBuffer.toString());

	}

	@Override
	protected void validateBeforeUpdate(String id, Category_Master inE) throws RuntimeException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {
		// TODO Auto-generated method stub

	}

}
