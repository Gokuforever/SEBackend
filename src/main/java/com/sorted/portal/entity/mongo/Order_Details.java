package com.sorted.portal.entity.mongo;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sorted.portal.beans.Item;
import com.sorted.portal.enums.OrderStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "order_details")
public class Order_Details extends BaseMongoEntity<String> {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String user_id;
	private String user_code;
	private List<Item> items;
	private BigDecimal total;
	private String pg_order_id;
	private Integer status_id;
	private OrderStatus status;
	private String transaction_id;

}
