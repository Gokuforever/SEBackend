package com.sorted.commons.entity.mongo;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import com.sorted.commons.beans.Order_Status_History;
import com.sorted.commons.beans.Return_Details;
import com.sorted.commons.enums.OrderStatus;
import com.sorted.commons.enums.PurchaseType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "order_items")
public class Order_Item extends BaseMongoEntity<String> {
	/**
	* 
	*/
	@Serial
	private static final long serialVersionUID = 1L;
	private String order_id;
	private String order_code;
	private String seller_id;
	private String seller_code;
	private String product_id;
	private String product_code;
	private String product_name;
	private Long quantity;
	private Long selling_price;
	private Long total_cost;
	private PurchaseType type;
	private Long estimated_secure_amount;
	private Long actual_secure_amount;
	private String item_review_remarks;
	private Integer secure_item_rating;
	private String bundle_id;
	private Integer status_id;
	private OrderStatus status;
	private LocalDateTime return_date;
	private List<Order_Status_History> status_history;
	private Order_Status_History latest_order_history;
	private Return_Details return_details;

	public void setStatus(@NonNull OrderStatus status, String cud_by) {
		Order_Status_History order_Status_History = Order_Status_History.builder().status(status)
				.modification_date(LocalDateTime.now()).modified_by(cud_by).build();
		List<Order_Status_History> list = CollectionUtils.isEmpty(getStatus_history()) ? new ArrayList<>()
				: getStatus_history();
		list.add(order_Status_History);
		setStatus_history(list);
		this.status = status;
	}

}
