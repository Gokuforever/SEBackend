package com.sorted.commons.entity.mongo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sorted.commons.enums.InputType;

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
	private boolean secure_item;
	private List<Groups> groups;

	@JsonIgnore
	public List<SubCategory> getSub_categories() {
		List<SubCategory> sub_cat = new ArrayList<>();
		if (CollectionUtils.isEmpty(this.groups)) {
			return sub_cat;
		}
		this.groups.stream().forEach(e -> {
			sub_cat.addAll(e.getSub_categories());
		});
		return sub_cat;
	}

	@Data
	public static class Groups {
		private String group_name;
		private Integer group_id;
		private Integer group_order;
		private List<SubCategory> sub_categories;

	}

	@Data
	public static class SubCategory {
		private String name;
		private List<String> attributes;
		private boolean mandate;
		private int order;
		private InputType input_type;
	}
}
