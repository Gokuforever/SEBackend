package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.NotifyRestockEntity;
import com.sorted.commons.helper.BaseMongoRepository;

public interface NotifyRestockRepository extends BaseMongoRepository<String, NotifyRestockEntity> {

    @Override
    default Class<NotifyRestockEntity> getEntityType() {
        return NotifyRestockEntity.class;
    }
}
