package com.sorted.commons.entity.service;

import com.sorted.commons.beans.BusinessHours;
import com.sorted.commons.entity.mongo.BaseMongoEntity;
import com.sorted.commons.entity.mongo.Seller;
import com.sorted.commons.enums.WeekDay;
import com.sorted.commons.helper.AggregationFilter;
import com.sorted.commons.repository.mongo.Seller_Repository;
import org.springframework.stereotype.Service;

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

    public Seller findById(String id) {
        AggregationFilter.SEFilter filter = new AggregationFilter.SEFilter(AggregationFilter.SEFilterType.AND);
        filter.addClause(AggregationFilter.WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
        filter.addClause(AggregationFilter.WhereClause.eq(BaseMongoEntity.Fields.id, id));
        return this.repoFindOne(filter);
    }
}
