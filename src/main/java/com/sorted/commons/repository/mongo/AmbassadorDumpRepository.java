package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.AmbassadorDumpEntity;
import com.sorted.commons.helper.BaseMongoRepository;

public interface AmbassadorDumpRepository extends BaseMongoRepository<String, AmbassadorDumpEntity> {

    @Override
    default Class<AmbassadorDumpEntity> getEntityType() {
        return AmbassadorDumpEntity.class;
    }
}
