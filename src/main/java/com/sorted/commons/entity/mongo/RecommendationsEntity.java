package com.sorted.commons.entity.mongo;

import com.sorted.commons.entity.beans.*;
import com.sorted.commons.enums.RecommenderType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "recommendations")
@Builder
public class RecommendationsEntity extends BaseMongoEntity<String> {

    @Field("product_id")
    private String productId;
    @Field("recommender_type")
    private RecommenderType recommenderType;
    @Field("recommender_details")
    private RecommenderDetails recommenderDetails;
    private Recommendation recommendation;
    @Field("professor_info")
    private ProfessorInfo professorInfo;
    @Field("student_info")
    private StudentInfo studentInfo;
    @Field("industry_expert_info")
    private IndustryExpertInfo industryExpertInfo;
    private Consent consent;
}
