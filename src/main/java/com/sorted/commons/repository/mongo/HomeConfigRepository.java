package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.HomeConfig;
import com.sorted.commons.helper.BaseMongoRepository;

public interface HomeConfigRepository extends BaseMongoRepository<String, HomeConfig> {
    @Override
    default Class<HomeConfig> getEntityType() {
        return HomeConfig.class;
    }
}
