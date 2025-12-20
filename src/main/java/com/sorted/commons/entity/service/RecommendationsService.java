package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.RecommendationsEntity;
import com.sorted.commons.repository.mongo.RecommendationsRepository;
import org.springframework.stereotype.Service;

@Service
public class RecommendationsService extends GenericEntityServiceImpl<String, RecommendationsEntity, RecommendationsRepository> {
    @Override
    protected Class<RecommendationsRepository> getRepoClass() {
        return RecommendationsRepository.class;
    }

    @Override
    protected void validateBeforeCreate(RecommendationsEntity inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeUpdate(String id, RecommendationsEntity inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeDelete(String id) throws RuntimeException {

    }
}
