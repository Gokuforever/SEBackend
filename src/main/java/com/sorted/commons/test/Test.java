package com.sorted.commons.test;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class Test {

	@Data
	@ToString(callSuper = true)
	class A {
		private String a1;
	}

	@Data
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = false)
	class B extends A {
		private String b;
	}

	public static void main(String[] args) {

	}
}
