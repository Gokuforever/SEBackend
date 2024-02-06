package com.sorted.portal.helper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.sorted.portal.config.StaticMongoAccessor;
import com.sorted.portal.entity.mongo.BaseMongoEntity;
import com.sorted.portal.enums.Operators;
import com.sorted.portal.helper.AggregationFilter.OrderBy;
import com.sorted.portal.helper.AggregationFilter.QueryFilter;
import com.sorted.portal.helper.AggregationFilter.QueryFilterType;
import com.sorted.portal.helper.AggregationFilter.SortOrder;
import com.sorted.portal.helper.AggregationFilter.WhereClause;

public interface BaseMongoRepository<K, T extends BaseMongoEntity<K>>
		extends BaseRepository<T, K>, MongoRepository<T, K> {

	@SuppressWarnings("unchecked")
	default Class<T> getEntityType() {
		Type genericSuperclass = getClass().getGenericSuperclass();

		if (genericSuperclass instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
			Type[] typeArguments = parameterizedType.getActualTypeArguments();

			if (typeArguments.length > 1 && typeArguments[1] instanceof Class) {
				return (Class<T>) typeArguments[1];
			}
		}
		throw new IllegalStateException("Unable to determine the entity type.");
	}

	@Override
	default List<T> repoFindAll() {
		return this.findAll();
	}

	@Override
	default T create(T obj, String cudby) {
		obj.setBeforeCreate(cudby);
		T insert = this.insert(obj);
		return insert;
	}

	@Override
	default T update(T obj, String cudby) {
		obj.setBeforeModification(cudby);
		return this.save(obj);
	}

	@Override
	default long totalCount() {
		return this.count();
	}

	@Override
	default T repoFindOne(QueryFilter f) {
		Query query = buildQuery(f);
//		return null;
		return StaticMongoAccessor.MONGO_TEMPLATE.findOne(query, getEntityType());
	}

	@Override
	default List<T> repoFind(QueryFilter f) {
		Query query = buildQuery(f);
//		return null;
		return StaticMongoAccessor.MONGO_TEMPLATE.find(query, getEntityType());
	}

	@Override
	default long countByFilter(QueryFilter f) {
		Query query = buildQuery(f);
//		return 0;
		return StaticMongoAccessor.MONGO_TEMPLATE.count(query, getEntityType());
	}

	@Override
	default void deleteOne(K id) {
		Optional<T> optional = this.findById(id);
		if (optional.isEmpty() || !optional.isPresent()) {
			return;
		}
		T t = optional.get();
		t.setDeleted(true);
		this.save(t);
	}

	public static Query buildQuery(QueryFilter filter) {
		Criteria criteria = buildCriteria(filter);

		Query query = new Query(criteria);

		if (filter.getOrderBy() != null) {
			query.with(buildSort(filter.getOrderBy()));
		}

		if (filter.getSelection() != null && !filter.getSelection().isEmpty()) {
			query.fields().include(filter.getSelection().toArray(new String[0]));
		}

		return query;
	}

	private static Criteria buildCriteria(QueryFilter filter) {
		List<WhereClause> clauses = filter.getClause();
		Criteria criteria = new Criteria();

		if (clauses != null && !clauses.isEmpty()) {
			List<Criteria> whereCriterias = clauses.stream().map(BaseMongoRepository::buildWhereClauseCriteria)
					.collect(Collectors.toList());

			if (filter.getType() == QueryFilterType.AND) {
				criteria.andOperator(whereCriterias.toArray(new Criteria[0]));
			} else if (filter.getType() == QueryFilterType.OR) {
				criteria.orOperator(whereCriterias.toArray(new Criteria[0]));
			}
		}

		return criteria;
	}

	private static Criteria buildWhereClauseCriteria(WhereClause clause) {
		Operators relation = clause.getOperator();
		switch (relation) {
		case LIKE:
			String valueL = Pattern.quote(clause.getValue());
			String regexL = String.format(".(%s).", valueL);
			return new Criteria(clause.getField()).regex(regexL, "i");
		case NOT_LIKE:
			String valueNL = Pattern.quote(clause.getValue());
			String regexNL = String.format(".(%s).", valueNL);
			return new Criteria(clause.getField()).not().regex(regexNL, "i");
		case EQUALS:
			return new Criteria(clause.getField()).is(clause.getValue());
		case NOT_EQUALS:
			return new Criteria(clause.getField()).ne(clause.getValue());
		case LESS_THAN:
			return new Criteria(clause.getField()).lt(clause.getValue());
		case GREATER_THAN:
			return new Criteria(clause.getField()).gt(clause.getValue());
		case IN:
			return new Criteria(clause.getField()).in(clause.getValues());
		default:
			return null;
		}

	}

	private static Sort buildSort(OrderBy orderBy) {
		Sort.Direction direction = orderBy.getType() == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
		return Sort.by(direction, orderBy.getKey());
	}

}
