package com.sorted.commons.entity.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sorted.commons.beans.Order_Status_History;
import com.sorted.commons.beans.Refund_Details;
import com.sorted.commons.enums.PurchaseType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "order_items")
public class Order_Item extends BaseMongoEntity<String> {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private String order_id;
	private String order_code;
	private String product_id;
	private String product_code;
	private Long quantity;
	private Long selling_price;
	private Long total_cost;
	private PurchaseType type;
	private String bundle_id;
	private Integer status_id;
	private String status;
	private List<Order_Status_History> status_history;
	private Order_Status_History latest_order_history;
	private Refund_Details refund_details;

}
