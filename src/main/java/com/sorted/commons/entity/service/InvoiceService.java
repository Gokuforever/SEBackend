package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.Invoice;
import com.sorted.commons.repository.mongo.InvoiceRepository;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService extends GenericEntityServiceImpl<String, Invoice, InvoiceRepository> {
    @Override
    protected Class<InvoiceRepository> getRepoClass() {
        return InvoiceRepository.class;
    }

    @Override
    protected void validateBeforeCreate(Invoice inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeUpdate(String id, Invoice inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeDelete(String id) throws RuntimeException {

    }
}
