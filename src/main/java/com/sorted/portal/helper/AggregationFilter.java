package com.sorted.portal.helper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sorted.portal.enums.Operators;
import com.sorted.portal.enums.ResponseCode;
import com.sorted.portal.exceptions.CustomIllegalArgumentsException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

public class AggregationFilter {

	private static final Logger logger = LoggerFactory.getLogger(AggregationFilter.class);

	@Data
	public static class SEFilter {

		private List<WhereClause> clause;
		private SEFilterType type;
		private List<SEFilterNode> nodes;
		private SEFilterType subquery_type;
		private OrderBy orderBy;
		private List<String> selection;

		// Don't use below
		private List<JoinClause> joins;
		private List<SubqueryClause> subqueries;

		public SEFilter(SEFilterType type) {
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
	public static class SEFilterNode {
		private List<WhereClause> clause;
		private SEFilterType type;

		public SEFilterNode(SEFilterType type) {
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
		private List<?> valueList;
		private Operators operator;

		public WhereClause(@NonNull String field, @NonNull List<?> valueList, @NonNull Operators operator) {
			super();
			this.field = field;
			this.operator = operator;
			if (CollectionUtils.isEmpty(valueList)) {
				throw new CustomIllegalArgumentsException(ResponseCode.NOT_A_LIST);
			}
			this.valueClassType = "list";
			this.valueList = valueList;
		}

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

		public static WhereClause like(String field, Object value) {
			return new WhereClause(field, value, Operators.LIKE);
		}

		public static WhereClause notLike(String field, Object value) {
			return new WhereClause(field, value, Operators.NOT_LIKE);
		}

		public static WhereClause in(String field, List<?> value) {
			return new WhereClause(field, value, Operators.IN);
		}
		
		public static WhereClause all(String field, List<?> value) {
			return new WhereClause(field, value, Operators.ALL);
		}

		@JsonIgnore
		private static boolean isValuePrimitive(String invalueClassType) {
			if (StringUtils.hasLength(invalueClassType)) {
				List<String> arrPrimitives = Arrays.asList("String", "int", "Integer", "long", "Long", "double",
						"Double", "boolean", "Boolean", "BigDecimal", "LocalDateTime");
				return arrPrimitives.contains(invalueClassType);
			}
			return false;
		}

		@JsonIgnore
		public Object getValueAsObject() {
			if (WhereClause.isValuePrimitive(this.valueClassType) && StringUtils.hasLength(this.value)) {
				try {

					switch (this.valueClassType) {
					case "String":
						return this.value;

					case "int":
					case "Integer":
						return Integer.parseInt(this.value);

					case "long":
					case "Long":
						return Long.parseLong(this.value);

					case "double":
					case "Double":
						return Double.parseDouble(this.value);

					case "boolean":
					case "Boolean":
						return Boolean.parseBoolean(this.value);

					case "BigDecimal":
						return BigDecimal.valueOf(Double.valueOf(this.value));

					case "LocalDateTime":
						return LocalDateTime.parse(this.value);
					default:
						return null;
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
					return null;
				}
			}
			return null;
		}

	}

	@Getter
	@AllArgsConstructor
	public enum SEFilterType {
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
