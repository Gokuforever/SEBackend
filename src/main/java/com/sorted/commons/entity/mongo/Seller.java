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
@Document(collection = "seller")
public class Seller extends BaseMongoEntity<String> {

	private static final long serialVersionUID = 7105004513020596023L;

	private String name;
	private String code;
	private Address address;
	private List<String> operational_pin_codes;
	private String contact_number;
	private String contact_email;
	private String alternate_contact_number;
	private String business_registration_number;
	private String gst_number;
}
