package com.sorted.commons.beans;

import lombok.Data;

import java.util.List;

@Data
public class EducationCategoryField {
    private String alias;
    private int order;
    private String type;
    private List<String> options;
    private boolean mandatory;
    private String description;
    private String filterable;

}
