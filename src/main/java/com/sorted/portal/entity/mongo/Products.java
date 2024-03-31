package com.sorted.portal.entity.mongo;

import java.math.BigDecimal;
import java.util.List;

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
	private BigDecimal selling_price;
	private String description;
	private Long quantity;
	private String category_id;
	private String category_code;
	private String seller_id;
	private String seller_code;
	private List<SelectedSubCatagories> selected_sub_catagories;

	@Data
	@FieldNameConstants
	public static class SelectedSubCatagories{
		private String sub_category;
		private List<String> selected_attributes;
	}
}
