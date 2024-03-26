package com.sorted.portal.entity.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "users")
public class Users extends BaseMongoEntity<String> {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private String code;
	private String first_name;
	private String last_name;
	private String mobile_no;
	private String email_id;
	private String password;
	private String profile_picture_id;
	private String role_id;
	private String role_code;
	private Boolean is_mobile_verified = false;
	private Boolean is_email_verified = false;
	private Integer status;

}
