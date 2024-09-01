package com.sorted.commons.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqBaseBean {

	private String req_user_id;
	private String req_role_id;
	private int page;
	private int size;
}
