package com.sorted.commons.beans;

import com.sorted.commons.enums.AddressType;

import lombok.Data;

@Data
public class Address {

	private String street;
	private String city;
	private String state;
	private String pincode;
	private String landmark;
	private AddressType address_type;
	private String address_type_desc;
	private Boolean is_default;
}
