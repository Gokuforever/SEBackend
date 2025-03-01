package com.sorted.commons.beans;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class AddressDTO implements Serializable{

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	private String id;
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
	private BigDecimal lat;
	private BigDecimal lng;
	private String phone_no;
}
