package com.example.test.entity.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.example.test.entity.BaseMySqlEntity;
import com.example.test.helper.AggregationFilter.QueryFilter;
import com.example.test.helper.BaseRepository;

public abstract class GenericEntityServiceImpl<K, T extends BaseMySqlEntity, R extends BaseRepository<T, K>>
		implements BaseRepository<T, K> {

	protected abstract Class<R> getRepoClass();

	private R repository;

	@Autowired
	private void setApplicationContext(ApplicationContext applicationContext) {
		repository = applicationContext.getBean(getRepoClass());
	}

	@Override
	public List<T> repoFindAll() {
		return repository.repoFindAll();
	}

	@Override
	public List<T> repoFind(QueryFilter filter) {
		return repository.repoFind(filter);
	}

	@Override
	public T repoFindOne(QueryFilter filter) {
		return repository.repoFindOne(filter);
	}

	@Override
	public long count() {
		return repository.count();
	}

	@Override
	public T create(T entity, String cudby) {
		return repository.create(entity, cudby);
	}

	@Override
	public long countByFilter(QueryFilter filter) {
		return repository.countByFilter(filter);
	}

	@Override
	public T update(T entity, String cudby) {
		return repository.update(entity, cudby);
	}

	@Override
	public void deleteOne(K id) {
		repository.deleteOne(id);
	}

}
