package com.sorted.portal.test;

import lombok.Data;
import lombok.ToString;

public class Test {

	@Data
	@ToString(callSuper = true)
	class A {
		private String a1;
		private B b;
	}

	@Data
	@ToString(callSuper = true)
	class B {
		private String b2;
		private A a;
	}

	public static void main(String[] args) {
		Test ta = new Test();
		A a = ta.new A();
		A a1 = ta.new A();
		B b = ta.new B();
		B b1 = ta.new B();
		a.setA1("a");
		a.setB(b);

		a1.setA1("a1");
		a1.setB(b1);
		b.setA(a1);
		b.setB2("b");
		
		System.out.println(a);
		System.out.println(a1);
		System.out.println(b);
		System.out.println(b1);
		System.out.println(ta);
	}
}
