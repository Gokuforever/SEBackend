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
	private BigDecimal selling_price;
	private String description;
	private Long quantity;

}
