package com.example.test.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.test.entity.mysql.BaseMySqlEntity;
import com.example.test.helper.AggregationFilter.JoinClause;
import com.example.test.helper.AggregationFilter.QueryFilter;
import com.example.test.helper.AggregationFilter.QueryFilterType;
import com.example.test.helper.AggregationFilter.WhereClause;

import lombok.NonNull;

public interface BaseMySqlRepository<K, T extends BaseMySqlEntity>
		extends BaseRepository<T, K>, JpaSpecificationExecutor<T>, JpaRepository<T, K> {

	@Override
	default T repoFindOne(QueryFilter f) {
		Optional<T> optional = this.findByFilterForOne(f);
		if (optional.isEmpty()) {
			return null;
		}
		return optional.get();
	}

	@Override
	default T create(T obj, String cudby) {
		obj.setBeforeCreate(cudby);
		return this.save(obj);
	}

	@Override
	default List<T> repoFind(QueryFilter f) {
		Specification<T> specification = this.buildSpecification(f);
		return this.findAll(specification);
	}

	@Override
	default List<T> repoFindAll() {
		return this.findAll();
	}

	@Override
	default long totalCount() {
		return this.count();
	}

	@Override
	default long countByFilter(QueryFilter f) {
		Specification<T> specification = this.buildSpecification(f);
		return this.count(specification);
	}

	default Optional<T> findByFilterForOne(QueryFilter f) {
		Specification<T> specification = this.buildSpecification(f);
		this.buildSpecification(f);
		return this.findOne(specification);
	}

	@Override
	default T update(T obj, String cudby) {
		obj.setBeforeCreate(cudby);
		return this.save(obj);
	}

	@Override
	default void deleteOne(K id) {
		Optional<T> findById = this.findById(id);
		if (findById.isEmpty()) {
			// throw exception
			return;
		}
		T t = findById.get();
		t.setDeleted(true);
		this.save(t);
	}

//	public default Specification<T> buildSpecification(Filter inputData) {
//		return new Specification<T>() {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
//				return BaseMySqlRepository.toPredicate(root, query, criteriaBuilder, inputData);
//			}
//		};
//	}

	public default Specification<T> buildSpecification(@NonNull QueryFilter inputData) {
		return new Specification<T>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				return BaseMySqlRepository.buildSpecificationQuery(root, inputData, criteriaBuilder);
			}
		};
	}

	private static <T> Predicate buildSpecificationQuery(Root<T> root, QueryFilter aggregationFilter,
			CriteriaBuilder criteriaBuilder) {
		List<Predicate> predicates = new ArrayList<>();

		if (aggregationFilter.getClause() != null) {
			for (WhereClause filterClause : aggregationFilter.getClause()) {
				predicates.add(buildPredicate(filterClause, root, criteriaBuilder));
			}
		}

		if (aggregationFilter.getJoins() != null) {
			for (AggregationFilter.JoinClause joinClause : aggregationFilter.getJoins()) {
				predicates.add(buildJoinPredicate(joinClause, root, criteriaBuilder));
			}
		}

		if (aggregationFilter.getType() == QueryFilterType.AND) {
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		} else {
			return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
		}
	}

	private static <T> Predicate buildPredicate(WhereClause filterClause, Root<T> root,
			CriteriaBuilder criteriaBuilder) {
		// Implement your logic to build predicates based on filterClause
		// Example:

		String field = filterClause.getField();
		String value = filterClause.getValue();

		switch (filterClause.getOperator()) {
		case EQUALS:
			return criteriaBuilder.equal(root.get(field), value);

		case NOT_EQUALS:
			return criteriaBuilder.notEqual(root.get(field), value);

		case GREATER_THAN:
			return criteriaBuilder.greaterThan(root.get(field), value);

		case LESS_THAN:
			return criteriaBuilder.lessThan(root.get(field), value);

		case LIKE:
//			criteriaBuilder.like(root.get(field), "%" + value + "%");
			return criteriaBuilder.like(root.get(field), "%" + value + "%");

		case IN:
			In<Object> inClause = criteriaBuilder.in(root.get(field));
			for (Object val : filterClause.getValues()) {
				inClause.value(val);
			}
			return inClause;
		default:
			throw new RuntimeException("Operation not supported yet");
		}
//		return criteriaBuilder.equal(root.get(filterClause.getField()), filterClause.getValue());
	}

	private static <T> Predicate buildJoinPredicate(JoinClause joinClause, Root<T> root,
			CriteriaBuilder criteriaBuilder) {
		String joinToEntity = joinClause.getJoinToEntity();
		String joinOnField = joinClause.getJoinOnField();

		// Example: INNER JOIN
		if (joinClause.getJoinType() == AggregationFilter.JoinType.INNER) {
			return criteriaBuilder.equal(root.get(joinOnField).get(joinToEntity).get("id"), root.get("id"));
		}
		// Example: LEFT JOIN
		else if (joinClause.getJoinType() == AggregationFilter.JoinType.LEFT) {
			return criteriaBuilder
					.and(criteriaBuilder.or(criteriaBuilder.isNull(root.get(joinOnField).get(joinToEntity)),
							criteriaBuilder.equal(root.get(joinOnField).get(joinToEntity).get("id"), root.get("id"))));
		}
		// Handle other join types as needed

		// Default: No join predicate
		return criteriaBuilder.conjunction();
	}

//	private static <T> Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder,
//			Filter input) {
//		String field = input.getField();
//		String value = input.getValue();
//
//		switch (input.getOperator()) {
//		case EQUALS:
//			return criteriaBuilder.equal(root.get(field), value);
//
//		case NOT_EQUALS:
//			return criteriaBuilder.notEqual(root.get(field), value);
//
//		case GREATER_THAN:
//			return criteriaBuilder.greaterThan(root.get(field), value);
//
//		case LESS_THAN:
//			return criteriaBuilder.lessThan(root.get(field), value);
//
//		case LIKE:
//			criteriaBuilder.like(root.get(field), "%" + value + "%");
//			return criteriaBuilder.like(root.get(field), "%" + value + "%");
//
//		case IN:
//			In<Object> inClause = criteriaBuilder.in(root.get(field));
//			for (Object val : input.getValues()) {
//				inClause.value(val);
//			}
//			return inClause;
//		default:
//			throw new RuntimeException("Operation not supported yet");
//		}
//	}

}
