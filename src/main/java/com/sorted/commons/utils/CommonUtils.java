package com.sorted.commons.utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommonUtils {

	public static boolean isBoolean(Boolean val) {
		return val != null;
	}

	public static long getNanoseconds() {
		LocalDateTime now = LocalDateTime.now();
		// Calculate nanoseconds since midnight
		return now.toLocalTime().toNanoOfDay();
	}

	public static <T> List<T> convertS2L(Set<T> set) {
		return set.stream().collect(Collectors.toList());
	}

	public static String generateFixedLengthRandomNumber(int length) {
		if (length > 18) {
			throw new IllegalStateException("To many digits");
		}
		int powInt = length - 1;
		int tLen = (int) Math.pow(10, powInt) * 9;
		int number = (int) (Math.random() * tLen) + (int) Math.pow(10, powInt) * 1;
		String randomNumber = String.valueOf(number);
		if (randomNumber.length() != length) {
			throw new IllegalStateException("The random number '" + randomNumber + "' is not '" + length + "' digits");
		}
		return randomNumber;
	}

}
