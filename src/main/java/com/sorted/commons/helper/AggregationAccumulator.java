package com.sorted.commons.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an accumulation operation for MongoDB aggregation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregationAccumulator {
    private String field;
    private AccumulatorOperation operation;
    
    public enum AccumulatorOperation {
        SUM, AVG, COUNT, MAX, MIN
    }
}