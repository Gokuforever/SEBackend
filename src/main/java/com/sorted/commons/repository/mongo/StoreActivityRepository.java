package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.StoreActivity;
import com.sorted.commons.helper.BaseMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreActivityRepository extends BaseMongoRepository<String, StoreActivity> {

    @Override
    default Class<StoreActivity> getEntityType() {
        return StoreActivity.class;
    }
}
