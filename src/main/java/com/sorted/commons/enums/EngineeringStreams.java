package com.sorted.commons.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum EngineeringStreams {

	ELECTRICAL_ENGINEERING(1, "Electrical Engineering"), MECHANICAL_ENGINEERING(2, "Mechanical Engineering"),
	CIVIL_ENGINEERING(3, "Civil Engineering"), COMPUTER_SCIENCE(4, "Computer Science"),
	AEROSPACE_ENGINEERING(5, "Aerospace Engineering"), CHEMICAL_ENGINEERING(6, "Chemical Engineering"),
	AUTOMOBILE_ENGINEERING(7, "Automobile Engineering"),
	ELECTRONICS_AND_COMMUNICATION(8, "Electronics and Communication"), ROBOTICS(9, "Robotics"),
	INFORMATION_TECHNOLOGY(10, "Information Technology"), OTHER(11, "Other");

	private int id;
	private String branch_name;

	private static Map<String, EngineeringStreams> by_stream = new HashMap<>();
	private static Map<Integer, EngineeringStreams> by_id = new HashMap<>();

	static {
		for (EngineeringStreams es : values()) {
			by_stream.put(es.getBranch_name(), es);
		}
	}

	public static EngineeringStreams getByName(@NonNull String k) {
		try {
			if (by_stream.containsKey(k)) {
				return by_stream.get(k);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static EngineeringStreams getById(@NonNull Integer k) {
		try {
			if (by_id.containsKey(k)) {
				return by_id.get(k);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
