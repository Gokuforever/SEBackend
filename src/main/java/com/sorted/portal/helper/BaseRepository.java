package com.sorted.portal.helper;

import java.util.List;

import com.sorted.portal.helper.AggregationFilter.QueryFilter;

public interface BaseRepository<T , K> {

	List<T> repoFindAll();

	T repoFindOne(QueryFilter f);

	List<T> repoFind(QueryFilter f);

	T create(T obj, String cudby);

	long countByFilter(QueryFilter f);

	long totalCount();

	T update(T obj, String cudby);

	void deleteOne(K id);
	
}
