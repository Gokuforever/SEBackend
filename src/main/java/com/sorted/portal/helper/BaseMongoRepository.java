package com.sorted.portal.helper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.util.CollectionUtils;

import com.sorted.portal.config.StaticMongoAccessor;
import com.sorted.portal.entity.mongo.BaseMongoEntity;
import com.sorted.portal.enums.Operators;
import com.sorted.portal.enums.ResponseCode;
import com.sorted.portal.exceptions.CustomIllegalArgumentsException;
import com.sorted.portal.helper.AggregationFilter.OrderBy;
import com.sorted.portal.helper.AggregationFilter.SEFilter;
import com.sorted.portal.helper.AggregationFilter.SEFilterNode;
import com.sorted.portal.helper.AggregationFilter.SEFilterType;
import com.sorted.portal.helper.AggregationFilter.SortOrder;
import com.sorted.portal.helper.AggregationFilter.WhereClause;

public interface BaseMongoRepository<K, T extends BaseMongoEntity<K>>
		extends BaseRepository<T, K>, MongoRepository<T, K> {

	static Logger logger = LoggerFactory.getLogger(AggregationFilter.class);

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
		return this.insert(obj);
	}

	@Override
	default T update(K id, T obj, String cudby) {
		Optional<T> optional = this.findById(id);
		if (!optional.isPresent()) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_ID);
		}
		T t = optional.get();
		obj.setCreated_by(t.getCreated_by());
		obj.setCreation_date(t.getCreation_date());
		obj.setBeforeModification(cudby);
		return this.save(obj);
	}

	@Override
	default long totalCount() {
		return this.count();
	}

	@Override
	default T repoFindOne(SEFilter f) {
		Query query = buildQuery(f);
		logger.info("query:: " + query);
		return StaticMongoAccessor.MONGO_TEMPLATE.findOne(query, getEntityType());
	}

	@Override
	default List<T> repoFind(SEFilter f) {
		Query query = buildQuery(f);
		logger.info("query:: " + query);
		return StaticMongoAccessor.MONGO_TEMPLATE.find(query, getEntityType());
	}

	@Override
	default long countByFilter(SEFilter f) {
		Query query = buildQuery(f);
		logger.info("query:: " + query);
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

	public static Query buildQuery(SEFilter filter) {
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

	private static Criteria buildCriteria(SEFilter filter) {
		List<WhereClause> clauses = filter.getClause();
		Criteria criteria = new Criteria();
		List<Criteria> nodeCriterias = new ArrayList<>();

		if (!CollectionUtils.isEmpty(clauses)) {
			List<Criteria> whereCriterias = clauses.stream().map(BaseMongoRepository::buildWhereClauseCriteria)
					.collect(Collectors.toList());

			if (filter.getType() == SEFilterType.AND) {
				criteria.andOperator(whereCriterias.toArray(new Criteria[0]));
			} else if (filter.getType() == SEFilterType.OR) {
				criteria.orOperator(whereCriterias.toArray(new Criteria[0]));
			}
		}
		if (!CollectionUtils.isEmpty(filter.getNodes())) {
			nodeCriterias = filter.getNodes().stream().map(BaseMongoRepository::buildCriteria)
					.collect(Collectors.toList());

		}
		if (!nodeCriterias.isEmpty()) {
			if (filter.getSubquery_type() == null) {
				filter.setSubquery_type(SEFilterType.AND);
			}
			filter.getSubquery_type();
			Criteria[] arrComb = nodeCriterias.toArray(new Criteria[nodeCriterias.size()]);
			if (filter.getSubquery_type().equals(SEFilterType.AND)) {
				criteria.andOperator(arrComb);
			} else if (filter.getSubquery_type().equals(SEFilterType.OR)) {
				criteria.orOperator(arrComb);
			}
		}
		return criteria;
	}

	private static Criteria buildCriteria(SEFilterNode node) {
		Criteria criteria = new Criteria();

		if (!CollectionUtils.isEmpty(node.getClause())) {
			List<Criteria> whereCriterias = node.getClause().stream().map(BaseMongoRepository::buildWhereClauseCriteria)
					.collect(Collectors.toList());

			if (node.getType() == SEFilterType.AND) {
				criteria.andOperator(whereCriterias.toArray(new Criteria[0]));
			} else if (node.getType() == SEFilterType.OR) {
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
			String regexL = String.format(".*(%s).*", valueL);
			return new Criteria(clause.getField()).regex(regexL, "i");
		case NOT_LIKE:
			String valueNL = Pattern.quote(clause.getValue());
			String regexNL = String.format(".*(%s).*", valueNL);
			return new Criteria(clause.getField()).not().regex(regexNL, "i");
		case EQUALS:
			return new Criteria(clause.getField()).is(clause.getValueAsObject());
		case NOT_EQUALS:
			return new Criteria(clause.getField()).ne(clause.getValueAsObject());
		case LESS_THAN:
			return new Criteria(clause.getField()).lt(clause.getValueAsObject());
		case GREATER_THAN:
			return new Criteria(clause.getField()).gt(clause.getValueAsObject());
		case IN:
			return new Criteria(clause.getField()).in(clause.getValueList());
		case ALL:
			return new Criteria(clause.getField()).all(clause.getValueList());
		default:
			return null;
		}

	}

	private static Sort buildSort(OrderBy orderBy) {
		Sort.Direction direction = orderBy.getType() == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
		return Sort.by(direction, orderBy.getKey());
	}

}
