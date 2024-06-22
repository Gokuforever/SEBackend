package com.sorted.commons.helper;

import java.util.List;

import com.sorted.commons.helper.AggregationFilter.SEFilter;

public interface BaseRepository<T, K> {

	List<T> repoFindAll();

	T repoFindOne(SEFilter f);

	List<T> repoFind(SEFilter f);

	T create(T obj, String cudby);

	long countByFilter(SEFilter f);

	long totalCount();

	T update(K id,T obj, String cud_by);

	void deleteOne(K id);

}
