package com.example.test.helper;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.test.entity.mongo.BaseMongoEntity;
import com.example.test.enums.Operators;
import com.example.test.helper.AggregationFilter.OrderBy;
import com.example.test.helper.AggregationFilter.QueryFilter;
import com.example.test.helper.AggregationFilter.QueryFilterType;
import com.example.test.helper.AggregationFilter.SortOrder;
import com.example.test.helper.AggregationFilter.WhereClause;

public interface BaseMongoRepository<K, T extends BaseMongoEntity<K>>
		extends BaseRepository<T, K>, MongoRepository<T, K> {

//	Class<T> getEntityClassForManualQuery();

	@Override
	default List<T> repoFindAll() {
		return this.findAll();
	}

	@Override
	default T create(T obj, String cudby) {
		obj.setBeforeCreate(cudby);
		return this.insert(obj);
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
		return null;
//		return StaticMongoAccessor.MONGO_TEMPLATE.findOne(query, );
	}

	@Override
	default List<T> repoFind(QueryFilter f) {
		Query query = buildQuery(f);
		return null;
//		return StaticMongoAccessor.MONGO_TEMPLATE.find(query, );
	}

	@Override
	default long countByFilter(QueryFilter f) {
		Query query = buildQuery(f);
		return 0;
//		return StaticMongoAccessor.MONGO_TEMPLATE.count(query, );
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
