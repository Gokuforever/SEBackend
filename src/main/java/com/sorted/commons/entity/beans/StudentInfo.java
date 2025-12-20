package com.sorted.commons.entity.beans;

import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
public class StudentInfo {

    private String degree;
    private String college;
    @Field("current_year")
    private String currentYear;
    @Field("graduation_year")
    private String graduationYear;
    private String cgpa;
}
