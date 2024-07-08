package com.sorted.commons.enums;

import java.util.HashMap;
import java.util.Map;

import com.sorted.commons.entity.mongo.Otp;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum EntityDetails {

	OTP(Otp.class);

	private Class<?> class_name;

	public static final Map<Class<?>, EntityDetails> byValue = new HashMap<>();

	static {
		for (EntityDetails ed : values()) {
			byValue.put(ed.getClass_name(), ed);
		}
	}

	public static void assertExists(@NonNull Class<?> clazz) {
		if (!byValue.containsKey(clazz)) {
			throw new CustomIllegalArgumentsException(ResponseCode.ENTITY_DEFINATION_INCOMPLETE);
		}
	}
}
