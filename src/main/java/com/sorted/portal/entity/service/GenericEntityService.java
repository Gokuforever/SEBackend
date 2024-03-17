package com.sorted.portal.entity.service;

import java.util.List;

import com.sorted.portal.helper.AggregationFilter.SEFilter;

public interface GenericEntityService<T, K> {

	List<T> findAll();

	List<T> find(SEFilter filter);

	T findOne(SEFilter filter);

	T create(T entity);

	long countByFilter(SEFilter filter);

	long count();

	T update(T entity);

	void deleteOne(K id);
}
