package com.sorted.commons.beans;

import lombok.Data;

@Data
public class Bank_Details {
	private String account_number;
	private String ifsc_code;
	private String bank_name;
	private String branch_name;
}
