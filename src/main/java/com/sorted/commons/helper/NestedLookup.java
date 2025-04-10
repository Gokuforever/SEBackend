package com.sorted.commons.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a nested lookup operation to be performed on data
 * that was already joined from another collection
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NestedLookup {
    private String fromCollection;  // Collection to join with
    private String localField;      // Field from the already joined data
    private String foreignField;    // Field from the target collection
    private String as;              // Alias for the nested joined data
    private boolean unwindBeforeLookup; // Whether to unwind the array before lookup
}