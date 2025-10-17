package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.WalletTransactionEntity;
import com.sorted.commons.helper.BaseMongoRepository;

public interface WalletTransactionRepository extends BaseMongoRepository<String, WalletTransactionEntity> {

    @Override
    default Class<WalletTransactionEntity> getEntityType() {
        return WalletTransactionEntity.class;
    }
}
