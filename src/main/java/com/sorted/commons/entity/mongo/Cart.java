package com.sorted.commons.entity.mongo;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sorted.commons.beans.Item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "cart")
public class Cart extends BaseMongoEntity<String> {
	/**
	* 
	*/
	@Serial
	private static final long serialVersionUID = 1L;
	private String user_id;
	private String promotion_id;
	private List<Item> cart_items;
	private BigDecimal total_price;
	private Long delivery_charges;

}
