package com.sorted.commons.repository;

import com.sorted.commons.entity.mongo.ZoneEntity;
import com.sorted.commons.helper.BaseMongoRepository;

public interface ZoneRepository extends BaseMongoRepository<String, ZoneEntity> {

    @Override
    default Class<ZoneEntity> getEntityType() {
        return ZoneEntity.class;
    }
}
