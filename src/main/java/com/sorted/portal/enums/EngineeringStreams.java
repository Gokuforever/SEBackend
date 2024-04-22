package com.sorted.portal.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum EngineeringStreams {

	ELECTRICAL_ENGINEERING("Electrical Engineering"), MECHANICAL_ENGINEERING("Mechanical Engineering"),
	CIVIL_ENGINEERING("Civil Engineering"), COMPUTER_SCIENCE("Computer Science"),
	AEROSPACE_ENGINEERING("Aerospace engineering"), CHEMICAL_ENGINEERING("Chemical Engineering"),
	AUTOMOBILE_ENGINEERING("Automobile Engineering"), ELECTRONICS_AND_COMMUNICATION("Electronics and Communication"),
	ROBOTICS("Robotics"), INFORMATION_TECHNOLOGY("Information Technology");

	private String branch_name;

	private static Map<String, EngineeringStreams> streamMap = new HashMap<>();

	static {
		for (EngineeringStreams es : values()) {
			streamMap.put(es.getBranch_name(), es);
		}
	}

	public static EngineeringStreams getByKey(@NonNull String k) {
		try {
			if (streamMap.containsKey(k)) {
				return streamMap.get(k);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
