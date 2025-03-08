package com.sorted.commons.entity.mongo;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Map;

import com.sorted.commons.enums.Gender;
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
	@Serial
	private static final long serialVersionUID = 1L;
	private String code;
	private String first_name;
	private String last_name;
	private String mobile_no;
	private String email_id;
	private String password;
	private String old_password;
	private String profile_picture_id;
	private String role_id;
	private Integer status;
	private String semester;
	private String branch;
	private String college;
	private String branch_desc;
	private Boolean is_verified = false;
	private LocalDateTime reset_pass_request_expiry;
	private String uuid;
	private boolean pass_changed;
	private Gender gender;
	private Map<String, String> properties;

}
