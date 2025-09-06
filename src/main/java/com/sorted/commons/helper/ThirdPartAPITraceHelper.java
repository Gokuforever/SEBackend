package com.sorted.commons.helper;

import com.sorted.commons.entity.service.ThirdPartyAPITraceService;
import com.sorted.commons.enums.ThirdPartyAPIType;
import com.sorted.commons.porter.req.beans.GetQuoteRequest;
import com.sorted.commons.utils.GsonUtils;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class ThirdPartAPITraceHelper {

    private final ThirdPartyAPITraceService traceService;

    public ThirdPartAPITraceHelper(ThirdPartyAPITraceService traceService) {
        this.traceService = traceService;
    }

    public <I, R> R runWithTrace(ThirdPartyAPIType requestType, I request, Supplier<R> supplier) {
        String input = GsonUtils.getGson().toJson(request);
        try {
            R res = supplier.get();
            traceService.saveToTrace(requestType, input, GsonUtils.getGson().toJson(res));
            return res;
        } catch (Exception e) {
            traceService.saveToErrorTrace(requestType, input, e.getMessage());
            throw e;
        }

    }
}
