package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.Order_Dump;
import com.sorted.commons.repository.mongo.Order_Dump_Repository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class Order_Dump_Service extends GenericEntityServiceImpl<String, Order_Dump, Order_Dump_Repository> {
    @Override
    protected Class<Order_Dump_Repository> getRepoClass() {
        return Order_Dump_Repository.class;
    }

    @Override
    protected void validateBeforeCreate(Order_Dump inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeUpdate(String id, Order_Dump inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeDelete(String id) throws RuntimeException {

    }

    @Async
    public void markSuccess(Order_Dump inE, String orderId, String cudBy) {
        inE.markSuccess(orderId);
        this.update(inE.getId(), inE, cudBy);
    }

    @Async
    public void markFailed(Order_Dump inE, String errorMessage, String responseCode, String cudBy) {
        inE.markFailed(errorMessage, responseCode);
        this.update(inE.getId(), inE, cudBy);
    }
}
