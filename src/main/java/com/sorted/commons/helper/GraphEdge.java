package com.sorted.commons.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an edge in a graph traversal between collections
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GraphEdge {
    private String targetCollection; // Target collection to join with
    private String sourceField;      // Field from the source collection
    private String targetField;      // Field from the target collection
    private String as;               // Alias for the joined data
    private boolean unwindAfterLookup; // Whether to unwind the array after lookup
}