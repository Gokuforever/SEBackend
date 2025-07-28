package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.EducationCategories;
import com.sorted.commons.helper.BaseMongoRepository;

public interface EducationCategoriesRepository extends BaseMongoRepository<String, EducationCategories> {

    @Override
    default Class<EducationCategories> getEntityType() {
        return EducationCategories.class;
    }
}
