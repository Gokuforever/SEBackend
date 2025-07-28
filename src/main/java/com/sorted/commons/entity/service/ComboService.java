package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.Combo;
import com.sorted.commons.repository.mongo.ComboRepository;
import org.springframework.stereotype.Service;

@Service
public class ComboService extends GenericEntityServiceImpl<String, Combo, ComboRepository> {
    @Override
    protected Class<ComboRepository> getRepoClass() {
        return ComboRepository.class;
    }

    @Override
    protected void validateBeforeCreate(Combo inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeUpdate(String id, Combo inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeDelete(String id) throws RuntimeException {

    }
}
