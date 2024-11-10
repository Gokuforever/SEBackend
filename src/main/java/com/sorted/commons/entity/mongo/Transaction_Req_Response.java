package com.sorted.commons.entity.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "transaction_req_response")
public class Transaction_Req_Response extends BaseMongoEntity<String> {

	private static final long serialVersionUID = 2363349911926719584L;
	private String request;
	private String response;

}
