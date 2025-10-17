package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.RewardRulesEntity;
import com.sorted.commons.helper.BaseMongoRepository;

public interface RewardRulesRepository extends BaseMongoRepository<String, RewardRulesEntity> {

    @Override
    default Class<RewardRulesEntity> getEntityType() {
        return RewardRulesEntity.class;
    }
}
