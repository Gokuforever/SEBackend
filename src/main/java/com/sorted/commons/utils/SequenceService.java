package com.sorted.commons.utils;

import com.sorted.commons.entity.service.CounterService;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

@Service
public class SequenceService {

    private final CounterService counterService;

    public SequenceService(CounterService counterService) {
        this.counterService = counterService;
    }

    private String getNextSequence(String name) {
        long sequence = counterService.getNextSequence(name);
        DecimalFormat df = new DecimalFormat("000000");
        return df.format(sequence);
    }

    public String generateId(String prefix) {
        return prefix + "-" + CommonUtils.getFormattedDateForId() + "-" + CommonUtils.generateRandomString(5) + getNextSequence(prefix);
    }
}
