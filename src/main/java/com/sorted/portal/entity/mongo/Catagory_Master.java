package com.sorted.portal.entity.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "catagory_master")
public class Catagory_Master extends BaseMongoEntity<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String catagory_code;
	private List<SubCatagory> sub_catagories;

	@Data
	public static class SubCatagory {
		private String name;
		private List<String> attributes;
	}
}
