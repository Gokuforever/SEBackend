package com.sorted.commons.utils;

import com.sorted.commons.entity.mongo.BaseMongoEntity;
import com.sorted.commons.entity.mongo.ReferralEntity;
import com.sorted.commons.entity.service.ReferralService;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import com.sorted.commons.exceptions.InvalidReferralCodeException;
import com.sorted.commons.helper.AggregationFilter.SEFilter;
import com.sorted.commons.helper.AggregationFilter.SEFilterType;
import com.sorted.commons.helper.AggregationFilter.WhereClause;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReferralUtility {

    private final ReferralService service;

    public void createReferral(String userId, String code) {

        if (!SERegExpUtils.isAlphaNumeric(code)) {
            throw new InvalidReferralCodeException();
        }

        SEFilter filter = new SEFilter(SEFilterType.AND);
        filter.addClause(WhereClause.eq(ReferralEntity.Fields.code, code));
        filter.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

        long count = service.countByFilter(filter);
        if (count > 0) {
            throw new CustomIllegalArgumentsException(ResponseCode.REFERRAL_CODE_EXISTS);
        }

        ReferralEntity referral = ReferralEntity.builder()
                .code(code)
                .userId(userId)
                .count(0)
                .active(true)
                .build();
        service.create(referral, userId);
    }

    public void refer(String userId, String code) {

        if (!SERegExpUtils.isAlphaNumeric(code)) {
            throw new InvalidReferralCodeException();
        }

        SEFilter filter = new SEFilter(SEFilterType.AND);
        filter.addClause(WhereClause.eq(ReferralEntity.Fields.code, code));
        filter.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

        ReferralEntity referralEntity = service.repoFindOne(filter);
        if (referralEntity == null) {
            throw new InvalidReferralCodeException();
        }

        SEFilter filter2 = new SEFilter(SEFilterType.AND);
        filter2.addClause(WhereClause.in(ReferralEntity.Fields.users, List.of(userId)));
        filter2.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

        ReferralEntity alreadyReferred = service.repoFindOne(filter2);
        if (alreadyReferred == null) {
            throw new CustomIllegalArgumentsException(ResponseCode.REFERRAL_NOT_APPLICABLE);
        }

        referralEntity.getUsers().add(userId);
        service.update(referralEntity.getId(), referralEntity, userId);
    }

    public void getReferredUsers(String userId) {
        SEFilter filter = new SEFilter(SEFilterType.AND);
        filter.addClause(WhereClause.in(ReferralEntity.Fields.users, List.of(userId)));
        filter.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
        service.repoFind(filter);
    }
}
