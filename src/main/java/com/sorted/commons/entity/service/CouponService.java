package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.CouponEntity;
import com.sorted.commons.repository.mongo.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class CouponService extends GenericEntityServiceImpl<String, CouponEntity, CouponRepository> {
    @Override
    protected Class<CouponRepository> getRepoClass() {
        return CouponRepository.class;
    }

    @Override
    protected void validateBeforeCreate(CouponEntity inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeUpdate(String id, CouponEntity inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeDelete(String id) throws RuntimeException {

    }
}
