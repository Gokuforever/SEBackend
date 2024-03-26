package com.sorted.portal.beans;

import lombok.Data;

@Data
public class UsersCUDReqBean {
	private String first_name;
	private String last_name;
	private String password;
	private String mobile_no;
	private String email_id;
}
