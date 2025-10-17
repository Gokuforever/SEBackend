package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.WalletEntity;
import com.sorted.commons.helper.BaseMongoRepository;

public interface WalletRepository extends BaseMongoRepository<String, WalletEntity> {

    @Override
    default Class<WalletEntity> getEntityType() {
        return WalletEntity.class;
    }
}
