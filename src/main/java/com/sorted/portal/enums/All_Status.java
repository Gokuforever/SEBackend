package com.sorted.portal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class All_Status {

	@Getter
	@AllArgsConstructor
	public static enum Role_Status {
		ACTIVE(1), INACTIVE(2), BLOCK(3);

		private int id;
	}
}
