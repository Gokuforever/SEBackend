package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.RecommendationsEntity;
import com.sorted.commons.helper.BaseMongoRepository;

public interface RecommendationsRepository extends BaseMongoRepository<String, RecommendationsEntity> {

    @Override
    default Class<RecommendationsEntity> getEntityType() {
        return RecommendationsEntity.class;
    }
}
