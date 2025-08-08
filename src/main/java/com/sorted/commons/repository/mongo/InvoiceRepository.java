package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Invoice;
import com.sorted.commons.helper.BaseMongoRepository;

public interface InvoiceRepository extends BaseMongoRepository<String, Invoice> {
    @Override
    default Class<Invoice> getEntityType() {
        return Invoice.class;
    }
}
