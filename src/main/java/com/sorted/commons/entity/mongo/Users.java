package com.sorted.commons.entity.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sorted.commons.beans.Address;

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
	private Integer status;
	private Integer semister;
	private Integer branch;
	private Boolean is_verified = false;
	private List<Address> addresses;

}
