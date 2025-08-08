package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.DemandingPincode;
import com.sorted.commons.helper.BaseMongoRepository;

public interface DemandingPincode_Repository extends BaseMongoRepository<String, DemandingPincode> {
    @Override
    default Class<DemandingPincode> getEntityType() {
        return DemandingPincode.class;
    }
}
