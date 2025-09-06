package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.ThirdPartyAPITraceEntity;
import com.sorted.commons.helper.BaseMongoRepository;

public interface ThirdPartyAPITraceRepository extends BaseMongoRepository<String, ThirdPartyAPITraceEntity> {

    @Override
    default Class<ThirdPartyAPITraceEntity> getEntityType() {
        return ThirdPartyAPITraceEntity.class;
    }
}
