package com.sorted.commons.beans;

import lombok.Data;

@Data
public class AddressDTO {

	private String code;
	private String street_1;
	private String street_2;
	private String landmark;
	private String city;
	private String state;
	private String pincode;
	private String address_type;
	private String address_type_desc;
	private Boolean is_default;
}
