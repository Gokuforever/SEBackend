package com.sorted.portal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Activity {

	MANAGE_USER(100, "Manage User");
	
	private int id;
	private String name;
	
}
