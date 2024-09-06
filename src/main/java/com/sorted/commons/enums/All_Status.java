package com.sorted.commons.enums;

import java.util.HashMap;
import java.util.Map;

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

	@Getter
	@AllArgsConstructor
	public enum Seller_Status {
		VERIFICATION_PENDING(1), ACTIVE(2), INACTIVE(3), BLOCKED(4);

		private int id;

		private static final Map<Integer, Seller_Status> valMap = new HashMap<>();

		static {
			for (Seller_Status s : values()) {
				valMap.put(s.getId(), s);
			}
		}

		public static Seller_Status getById(int id) {
			try {
				return valMap.get(id);
			} catch (Exception e) {
				return null;
			}
		}
	}
}
