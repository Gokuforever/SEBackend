package com.sorted.portal.entity.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sorted.portal.entity.mongo.Catagory_Master.SubCatagory;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "product_catagory_mapping")
public class Product_Catagory_Mapping extends BaseMongoEntity<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String product_id;
	private String catagory_name;
	private String catagory_code;
	private SubCatagory sub_catagory;
}
