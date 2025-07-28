package com.sorted.commons.repository.mongo;


import com.sorted.commons.entity.mongo.Combo;
import com.sorted.commons.helper.BaseMongoRepository;

public interface ComboRepository extends BaseMongoRepository<String, Combo> {

    @Override
    default Class<Combo> getEntityType() {
        return Combo.class;
    }
}
