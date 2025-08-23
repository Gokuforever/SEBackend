package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Permission {

	VIEW(1),
	EDIT(4);
	
	private final int id;
}
