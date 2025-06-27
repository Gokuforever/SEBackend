package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.WebhookTrace;
import com.sorted.commons.helper.BaseMongoRepository;

public interface WebhookTrace_Repository extends BaseMongoRepository<String, WebhookTrace> {

    @Override
    default Class<WebhookTrace> getEntityType() {
        return WebhookTrace.class;
    }
}
