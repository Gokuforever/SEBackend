package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.LaunchingEmail;
import com.sorted.commons.helper.BaseMongoRepository;

public interface LaunchingEmailRepository extends BaseMongoRepository<String, LaunchingEmail> {

    @Override
    default Class<LaunchingEmail> getEntityType() {
        return LaunchingEmail.class;
    }
}
