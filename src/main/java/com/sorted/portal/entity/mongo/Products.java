package com.sorted.portal.entity.mongo;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "products")
public class Products extends BaseMongoEntity<String> {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private String product_code;
	private String name;
	private BigDecimal price;
	private String description;
	private Long quantity;
//	private CatagoryDetails catagory;

//	@Data
//	public static class CatagoryDetails {
//		private String catagory_name;
//		private String catagory_code;
//		private List<String> catagory_mapping_ids;
//		private List<SubCatagory> selected_sub_catagories;
//	}

}
