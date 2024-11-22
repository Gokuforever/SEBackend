package com.sorted.commons.entity.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@Document(collection = "order_details")
public class Pincode_Master extends BaseMongoEntity<String> {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private String circlename;
	private String regionname;
	private String divisionname;
	private String district;
	private String statename;
	private int pincode;
	private double latitude;
	private double longitude;
}
