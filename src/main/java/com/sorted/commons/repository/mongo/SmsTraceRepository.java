package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.SmsTraceEntity;
import com.sorted.commons.helper.BaseMongoRepository;

public interface SmsTraceRepository extends BaseMongoRepository<String, SmsTraceEntity> {

    @Override
    default Class<SmsTraceEntity> getEntityType() {
        return SmsTraceEntity.class;
    }
}
