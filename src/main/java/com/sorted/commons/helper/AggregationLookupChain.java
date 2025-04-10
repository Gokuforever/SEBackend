package com.sorted.commons.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a chain of lookups starting from a main collection and potentially
 * continuing with nested lookups on the joined data
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregationLookupChain {
    private String fromCollection;  // Collection to join with
    private String localField;      // Field from main collection
    private String foreignField;    // Field from joined collection
    private String as;              // Alias for the joined data
    private List<NestedLookup> nestedLookups; // Subsequent lookups on the joined data
}