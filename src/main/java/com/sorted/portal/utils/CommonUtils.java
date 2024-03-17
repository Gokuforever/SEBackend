package com.sorted.portal.utils;

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
}
