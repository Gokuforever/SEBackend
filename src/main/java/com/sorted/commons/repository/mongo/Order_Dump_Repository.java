package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Order_Dump;
import com.sorted.commons.helper.BaseMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Order_Dump_Repository extends BaseMongoRepository<String, Order_Dump> {

    @Override
    default Class<Order_Dump> getEntityType() {
        return Order_Dump.class;
    }
}
