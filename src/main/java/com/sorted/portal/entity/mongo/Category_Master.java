package com.sorted.portal.entity.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "category_master")
public class Category_Master extends BaseMongoEntity<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String category_code;
	private List<SubCategory> sub_categories;

	@Data
	public static class SubCategory {
		private String name;
		private List<String> attributes;
	}
}
