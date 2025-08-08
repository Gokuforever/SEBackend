package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Counter;
import com.sorted.commons.helper.BaseMongoRepository;

public interface CounterRepository extends BaseMongoRepository<String, Counter> {
    @Override
    default Class<Counter> getEntityType() {
        return Counter.class;
    }
}
