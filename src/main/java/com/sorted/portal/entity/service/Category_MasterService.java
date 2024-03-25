package com.sorted.portal.entity.service;

import java.time.Year;

import org.springframework.stereotype.Service;

import com.sorted.portal.entity.mongo.Catagory_Master;
import com.sorted.portal.repository.mongo.Catagory_MasterRepository;
import com.sorted.portal.utils.CommonUtils;

@Service
public class Catagory_MasterService
		extends GenericEntityServiceImpl<String, Catagory_Master, Catagory_MasterRepository> {

	@Override
	protected Class<Catagory_MasterRepository> getRepoClass() {
		return Catagory_MasterRepository.class;
	}

	@Override
	protected void validateBeforeCreate(Catagory_Master inE) throws RuntimeException {
		long nanoseconds = CommonUtils.getNanoseconds();
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("CI-");
		stringBuffer.append(nanoseconds);
		stringBuffer.append("-");
		stringBuffer.append(Year.now());

		inE.setCatagory_code(stringBuffer.toString());

	}

	@Override
	protected void validateBeforeUpdate(String id, Catagory_Master inE) throws RuntimeException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validateBeforeDelete(String id) throws RuntimeException {
		// TODO Auto-generated method stub

	}

}
