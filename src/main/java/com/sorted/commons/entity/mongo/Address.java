package com.sorted.commons.entity.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sorted.commons.enums.AddressType;
import com.sorted.commons.enums.UserType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "address")
public class Address extends BaseMongoEntity<String> {

	private static final long serialVersionUID = 1L;
	private String code;
	private String street_1;
	private String street_2;
	private String landmark;
	private String city;
	private String state;
	private String pincode;
	private UserType user_type;
	private String entity_id;
	private AddressType address_type;
	private String address_type_desc;
	private Boolean is_default;
}
