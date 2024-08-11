package com.sorted.commons.beans;

import com.sorted.commons.enums.AddressType;

import lombok.Data;

@Data
public class Address {

	private String street_1;
	private String street_2;
	private String landmark;
	private String city;
	private String state;
	private String pincode;
	private AddressType address_type;
	private String address_type_desc;
	private Boolean is_default;
}
