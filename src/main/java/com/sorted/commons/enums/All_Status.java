package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class All_Status {

	@Getter
	@AllArgsConstructor
	public enum Role_Status {
		ACTIVE(1), INACTIVE(2), BLOCK(3);

		private int id;
	}
	
	@Getter
	@AllArgsConstructor
	public enum User_Status {
		ACTIVE(1), INACTIVE(2), BLOCK(3);

		private int id;
	}
}
