package com.sorted.portal.helper;

import java.math.BigDecimal;
import java.util.List;

import com.sorted.portal.enums.Operators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class Filter {
	private FilterType type;
	private String field;
	private String value;
	private String valueClassType;
	private List<String> values;
	private Operators operator;

	public void setValue(Object value) {
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

	public Filter(FilterType type) {
		this.type = type;
	}

	@Getter
	@AllArgsConstructor
	public enum FilterType {
		AND, OR
	}
}
