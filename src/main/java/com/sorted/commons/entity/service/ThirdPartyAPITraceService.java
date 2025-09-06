package com.sorted.commons.entity.service;

import com.sorted.commons.constants.Defaults;
import com.sorted.commons.entity.mongo.ThirdPartyAPITraceEntity;
import com.sorted.commons.enums.ThirdPartyAPIType;
import com.sorted.commons.repository.mongo.ThirdPartyAPITraceRepository;
import org.springframework.stereotype.Service;

@Service
public class ThirdPartyAPITraceService extends GenericEntityServiceImpl<String, ThirdPartyAPITraceEntity, ThirdPartyAPITraceRepository> {
    @Override
    protected Class<ThirdPartyAPITraceRepository> getRepoClass() {
        return ThirdPartyAPITraceRepository.class;
    }

    @Override
    protected void validateBeforeCreate(ThirdPartyAPITraceEntity inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeUpdate(String id, ThirdPartyAPITraceEntity inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeDelete(String id) throws RuntimeException {

    }

    public void saveToTrace(ThirdPartyAPIType requestType, String request, String response) {
        ThirdPartyAPITraceEntity traceEntity = ThirdPartyAPITraceEntity.builder()
                .requestType(requestType)
                .rawRequest(request)
                .rawResponse(response)
                .build();
        this.create(traceEntity, Defaults.AUTO);
    }

    public void saveToErrorTrace(ThirdPartyAPIType requestType, String request, String errorMessage) {
        ThirdPartyAPITraceEntity traceEntity = ThirdPartyAPITraceEntity.builder()
                .requestType(requestType)
                .rawRequest(request)
                .errorMessage(errorMessage)
                .build();
        this.create(traceEntity, Defaults.AUTO);
    }


}
