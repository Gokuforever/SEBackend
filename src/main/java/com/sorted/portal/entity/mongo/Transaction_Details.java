package com.sorted.portal.entity.mongo;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sorted.portal.enums.TransactionStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "payment_details")
public class Transaction_Details extends BaseMongoEntity<String> {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private String order_id;
	private String order_code;
	private String pg_transaction_id;
	private String bank_reference;
	private String payment_mode;
	private LocalDateTime payment_completion_time;
	private LocalDateTime payment_time;
	private String payment_currency;
	private String pg_name;
	private String payment_message;
	private String pg_status;
	private Integer status_id;
	private TransactionStatus status;

	public void setStatus(@NonNull TransactionStatus s) {
		this.status = s;
		this.status_id = s.getId();
	}
}
