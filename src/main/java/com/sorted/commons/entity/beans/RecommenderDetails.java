package com.sorted.commons.entity.beans;

import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
public class RecommenderDetails {

    @Field("full_name")
    private String fullName;
    private String email;
    private String phone;
    private String bio;
    private String photoUrl;
}
