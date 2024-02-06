package com.sorted.portal.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.sorted.portal.enums.Operators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

public class AggregationFilter {

	@Data
	public static class QueryFilter {

		private List<WhereClause> clause;
		private QueryFilterType type;
		private OrderBy orderBy;
		private List<String> selection;
		
		// Don't use below
		private List<JoinClause> joins;
		private List<SubqueryClause> subqueries;

		public QueryFilter(QueryFilterType type) {
			this.type = type;
		}

		public void addClause(List<WhereClause> clause) {
			if (CollectionUtils.isEmpty(this.clause)) {
				this.clause = new ArrayList<>();
			}
			this.clause.addAll(clause);
		}

		public void addClause(WhereClause clause) {
			if (CollectionUtils.isEmpty(this.clause)) {
				this.clause = new ArrayList<>();
			}
			this.clause.add(clause);
		}
	}

	@Data
	public static class WhereClause {
		private String field;
		private String value;
		private String valueClassType;
		private List<String> values;
		private Operators operator;

		public WhereClause(@NonNull String field, Object value, @NonNull Operators operator) {
			this.field = field;
			this.operator = operator;

			if (value != null) {
				Class<?> pValClass = value.getClass();
				this.valueClassType = pValClass.getSimpleName();

				// converting value
				if (pValClass == String.class) {
					this.value = (String) value;
				} else if (pValClass == java.time.LocalDateTime.class) {
					this.value = String.valueOf(value);
				} else if (pValClass == int.class || pValClass == Integer.class || pValClass == long.class
						|| pValClass == Long.class || pValClass == double.class || pValClass == Double.class
						|| pValClass == boolean.class || pValClass == Boolean.class || pValClass == BigDecimal.class) {
					this.value = String.valueOf(value);
				} else {
					this.value = null;
				}
			} else {
				this.value = null;
				this.valueClassType = null;
			}
		}

		public static WhereClause eq(String field, Object value) {
			return new WhereClause(field, value, Operators.EQUALS);
		}

		public static WhereClause notEq(String field, Object value) {
			return new WhereClause(field, value, Operators.NOT_EQUALS);
		}
	}

	@Getter
	@AllArgsConstructor
	public enum QueryFilterType {
		AND, OR
	}

	@Getter
	@AllArgsConstructor
	public enum SortOrder {
		ASC, DESC
	}

	@Data
	public class OrderBy {
		private SortOrder type = SortOrder.DESC;
		private String key;
	}

	@Data
	public static class JoinClause {
		private JoinType joinType; // INNER, LEFT, etc.
		private String joinOnField; // Field to join on
		private String joinToEntity; // Entity to join with
	}

	@Getter
	@AllArgsConstructor
	public enum JoinType {
		INNER, LEFT, RIGHT
	}

	@Data
	public static class SubqueryClause {
		private AggregationFilter subqueryFilter;
		private String correlatedField; // Field in the main query used for correlation
	}
}
