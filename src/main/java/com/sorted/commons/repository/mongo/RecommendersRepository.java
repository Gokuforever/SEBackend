package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.RecommendersEntity;
import com.sorted.commons.helper.BaseMongoRepository;

public interface RecommendersRepository extends BaseMongoRepository<String, RecommendersEntity> {

    @Override
    default Class<RecommendersEntity> getEntityType() {
        return RecommendersEntity.class;
    }
}
