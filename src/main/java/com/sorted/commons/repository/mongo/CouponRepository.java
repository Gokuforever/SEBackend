package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.CouponEntity;
import com.sorted.commons.helper.BaseMongoRepository;

public interface CouponRepository extends BaseMongoRepository<String, CouponEntity> {

    @Override
    default Class<CouponEntity> getEntityType() {
        return CouponEntity.class;
    }
}
