package com.sorted.portal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Permissions {

	VIEW(1),
	EDIT(2);
	
	private int id;
}
