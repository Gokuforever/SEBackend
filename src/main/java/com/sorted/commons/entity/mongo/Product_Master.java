package com.sorted.commons.entity.mongo;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "product_master")
public class Product_Master extends BaseMongoEntity<String> {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private String catagory_id;
	private String name;
	private String img_src;
	private Map<String, List<String>> attributes;
}
