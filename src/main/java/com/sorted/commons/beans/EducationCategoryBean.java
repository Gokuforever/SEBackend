package com.sorted.commons.beans;

import com.sorted.commons.entity.mongo.EducationCategories;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EducationCategoryBean {

    private String id;
    private String education_level;
    private List<EducationCategoryField> fields;

    public EducationCategoryBean(EducationCategories educationCategories) {
        this.education_level = educationCategories.getEducation_level();
        this.fields = educationCategories.getFields();
        this.id = educationCategories.getId();
    }
}
