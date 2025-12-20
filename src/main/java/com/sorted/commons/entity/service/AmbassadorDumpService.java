package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.AmbassadorDumpEntity;
import com.sorted.commons.repository.mongo.AmbassadorDumpRepository;
import org.springframework.stereotype.Service;

@Service
public class AmbassadorDumpService extends GenericEntityServiceImpl<String, AmbassadorDumpEntity, AmbassadorDumpRepository> {


    @Override
    protected Class<AmbassadorDumpRepository> getRepoClass() {
        return AmbassadorDumpRepository.class;
    }

    @Override
    protected void validateBeforeCreate(AmbassadorDumpEntity inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeUpdate(String id, AmbassadorDumpEntity inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeDelete(String id) throws RuntimeException {

    }
}
