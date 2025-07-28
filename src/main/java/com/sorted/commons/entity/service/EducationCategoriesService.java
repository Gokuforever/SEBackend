package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.EducationCategories;
import com.sorted.commons.repository.mongo.EducationCategoriesRepository;
import org.springframework.stereotype.Service;


@Service
public class EducationCategoriesService extends GenericEntityServiceImpl<String, EducationCategories, EducationCategoriesRepository> {
    @Override
    protected Class<EducationCategoriesRepository> getRepoClass() {
        return EducationCategoriesRepository.class;
    }

    @Override
    protected void validateBeforeCreate(EducationCategories inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeUpdate(String id, EducationCategories inE) throws RuntimeException {

    }

    @Override
    protected void validateBeforeDelete(String id) throws RuntimeException {

    }
}
