package com.sorted.commons.entity.mongo;

import java.io.Serial;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "user_auth_details")
public class User_Auth_Details extends BaseMongoEntity<String> {
	/**
	* 
	*/
	@Serial
	private static final long serialVersionUID = 1L;
	private String req_user_id;
	private String token;
	private String refresh_token;
	private LocalDateTime expiry_datetime;
}
