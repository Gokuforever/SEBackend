package com.sorted.commons.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a MongoDB lookup operation (similar to SQL JOIN)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregationLookup {
    private String fromCollection;  // Collection to join with
    private String localField;      // Field from main collection
    private String foreignField;    // Field from joined collection
    private String as;              // Alias for the joined data
}