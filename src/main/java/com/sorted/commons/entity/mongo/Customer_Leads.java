package com.sorted.commons.entity.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sorted.commons.enums.UserType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "customer_leads")
public class Customer_Leads extends BaseMongoEntity<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String first_name;
	private String last_name;
	private String mobile_no;
	private String email_id;
	private String password;
	private UserType user_type;
	private Integer branch;
	private Integer semister;

}
