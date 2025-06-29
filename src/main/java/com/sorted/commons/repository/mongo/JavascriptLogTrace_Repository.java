package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.JavascriptLogTrace;
import com.sorted.commons.helper.BaseMongoRepository;

public interface JavascriptLogTrace_Repository extends BaseMongoRepository<String, JavascriptLogTrace> {
    @Override
    default Class<JavascriptLogTrace> getEntityType() {
        return JavascriptLogTrace.class;
    }
}
