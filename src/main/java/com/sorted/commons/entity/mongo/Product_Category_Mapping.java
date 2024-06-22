package com.sorted.commons.entity.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sorted.commons.entity.mongo.Category_Master.SubCategory;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "product_category_mapping")
public class Product_Category_Mapping extends BaseMongoEntity<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String product_id;
	private String category_name;
	private String category_code;
	private SubCategory sub_category;
}
