package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Feedback;
import com.sorted.commons.helper.BaseMongoRepository;

public interface FeedbackRepository extends BaseMongoRepository<String, Feedback> {

    @Override
    default Class<Feedback> getEntityType() {
        return Feedback.class;
    }
}
