package com.sorted.commons.notifications.helper;

import com.sorted.commons.entity.service.SmsTraceService;
import com.sorted.commons.enums.SmsTemplate;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Service
public class SmsTraceHelper {

    private final SmsTraceService smsTraceService;

    public SmsTraceHelper(SmsTraceService smsTraceService) {
        this.smsTraceService = smsTraceService;
    }

    public void runWithTrace(List<String> mobileNumber, @NonNull String content, SmsTemplate smsTemplate, String createdBy, Supplier<String> supplier) {
        try {
            String res = supplier.get();
            smsTraceService.saveToTrace(mobileNumber, content, smsTemplate, res, createdBy);
        } catch (Exception e) {
            log.error("Error while sending SMS", e);
            smsTraceService.saveToErrorTrace(mobileNumber, content, smsTemplate, e, createdBy);
            throw e;
        }
    }
}
