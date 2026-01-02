package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.RecommendersEntity;
import com.sorted.commons.repository.mongo.RecommendersRepository;
import org.springframework.stereotype.Service;

@Service
public class RecommendersService extends GenericEntityServiceImpl<String, RecommendersEntity, RecommendersRepository> {
    @Override
    protected Class<RecommendersRepository> getRepoClass() {
        return RecommendersRepository.class;
    }

    @Override
    protected void validateBeforeCreate(RecommendersEntity inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeUpdate(String id, RecommendersEntity inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeDelete(String id) throws RuntimeException {

    }
}
