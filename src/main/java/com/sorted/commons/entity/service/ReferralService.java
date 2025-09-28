package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.ReferralEntity;
import com.sorted.commons.repository.mongo.ReferralRepository;
import org.springframework.stereotype.Service;

@Service
public class ReferralService extends GenericEntityServiceImpl<String, ReferralEntity, ReferralRepository> {
    @Override
    protected Class<ReferralRepository> getRepoClass() {
        return ReferralRepository.class;
    }

    @Override
    protected void validateBeforeCreate(ReferralEntity inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeUpdate(String id, ReferralEntity inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeDelete(String id) throws RuntimeException {

    }
}
