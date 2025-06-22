package com.sorted.commons.entity.service;

import com.sorted.commons.beans.BusinessHours;
import com.sorted.commons.enums.WeekDay;
import org.springframework.stereotype.Service;

import com.sorted.commons.entity.mongo.Seller;
import com.sorted.commons.repository.mongo.Seller_Repository;

import java.util.List;

@Service
public class Seller_Service extends GenericEntityServiceImpl<String, Seller, Seller_Repository> {

    @Override
    protected Class<Seller_Repository> getRepoClass() {
        return Seller_Repository.class;
    }

    @Override
    protected void validateBeforeCreate(Seller inE) throws RuntimeException {
        if (inE.getBusiness_hours() == null) {
            inE.setBusiness_hours(BusinessHours.builder().start_time(10).end_time(7).fixed_off_days(List.of(WeekDay.SUNDAY)).build());
        }
    }

    @Override
    protected void validateBeforeUpdate(String id, Seller inE) throws RuntimeException {
    }

    @Override
    protected void validateBeforeDelete(String id) throws RuntimeException {
    }

}
