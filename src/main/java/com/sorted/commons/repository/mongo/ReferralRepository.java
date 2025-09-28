package com.sorted.commons.repository.mongo;


import com.sorted.commons.entity.mongo.ReferralEntity;
import com.sorted.commons.helper.BaseMongoRepository;

public interface ReferralRepository extends BaseMongoRepository<String, ReferralEntity> {

    @Override
    default Class<ReferralEntity> getEntityType() {
        return ReferralEntity.class;
    }
}
