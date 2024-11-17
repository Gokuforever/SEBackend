package com.sorted.commons.entity.mongo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sorted.commons.beans.AddressDTO;
import com.sorted.commons.beans.DeliveryRequestAttempts;
import com.sorted.commons.beans.Order_Status_History;
import com.sorted.commons.enums.OrderStatus;
import com.sorted.commons.porter.res.beans.FetchOrderRes.FareDetails;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "order_details")
public class Order_Details extends BaseMongoEntity<String> {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private String code;
	private String user_id;
	private String payment_mode;
	private String pg_order_id;
	private Long total_amount;
	private OrderStatus status;
	private Integer status_id;
	private String transaction_id;
	private List<Order_Status_History> order_status_history;
	private AddressDTO delivery_address;
	private AddressDTO pickup_address;
	private String payment_status;
	private String shipment_status;
	private LocalDateTime expected_delivery_date;
	private Long actual_delivery_charges;
	private Long estimated_delivery_charges;
	private Long seller_share;
	private Boolean is_payout_done;
	private Long studeaze_share;
	private List<DeliveryRequestAttempts> delivery_request_attempts;
	private String dp_order_id; // delivery partner order id
	private LocalDateTime estimated_pickup_time;
	private FareDetails fare_details;

	@JsonIgnore
	public void setStatus(@NonNull OrderStatus status, String cud_by) {
		Order_Status_History order_Status_History = Order_Status_History.builder().status(status)
				.modification_date(LocalDateTime.now()).modified_by(cud_by).build();
		List<Order_Status_History> list = CollectionUtils.isEmpty(getOrder_status_history()) ? new ArrayList<>()
				: getOrder_status_history();
		list.add(order_Status_History);
		setOrder_status_history(list);
		this.status = status;
	}

	@JsonIgnore
	public void setFare_details(FareDetails fare_details) {
		if (compareHash(fare_details)) {
			this.fare_details = fare_details;
		}
	}

	@JsonIgnore
	private boolean compareHash(FareDetails details) {
		int hash = Objects.hash(fare_details);
		int hash2 = Objects.hash(details);
		return hash != hash2;
	}

}