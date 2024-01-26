package com.example.test.entity.service;

import java.util.List;

import com.example.test.helper.AggregationFilter.QueryFilter;

public interface GenericEntityService<T, K> {

	List<T> findAll();

	List<T> find(QueryFilter filter);

	T findOne(QueryFilter filter);

	T create(T entity);

	long countByFilter(QueryFilter filter);

	long count();

	T update(T entity);

	void deleteOne(K id);
}
