package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.Feedback;
import com.sorted.commons.repository.mongo.FeedbackRepository;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService extends GenericEntityServiceImpl<String, Feedback, FeedbackRepository> {
    @Override
    protected Class<FeedbackRepository> getRepoClass() {
        return FeedbackRepository.class;
    }

    @Override
    protected void validateBeforeCreate(Feedback inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeUpdate(String id, Feedback inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeDelete(String id) throws RuntimeException {

    }
}
