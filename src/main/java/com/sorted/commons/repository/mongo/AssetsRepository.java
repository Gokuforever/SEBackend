package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.AssetsEntity;
import com.sorted.commons.helper.BaseMongoRepository;

public interface AssetsRepository extends BaseMongoRepository<String, AssetsEntity> {

    @Override
    default Class<AssetsEntity> getEntityType() {
        return AssetsEntity.class;
    }
}
