package com.sorted.portal.utils;

import java.util.regex.Pattern;

import lombok.NonNull;

public class SERegExpUtils {

	public static Boolean isString(@NonNull String val) {
		val = val.trim();
		Pattern pattern = Pattern.compile("[a-zA-Z]*");
		return pattern.matcher(val).matches();
	}

	public static Boolean standardTextValidation(@NonNull String val) {
		val = val.strip();
		if (val.length() > 255) {
			return false;
		}
		Pattern pattern = Pattern.compile("^(?!.*--)[a-zA-Z0-9\\.\\-\\_\\'\\s]+$");
		return pattern.matcher(val).matches();
	}

	public static Boolean isQuantity(@NonNull String val) {
		val = val.strip();
		if (val.length() > 10) {
			return false;
		}
		Pattern pattern = Pattern.compile("\\d+");
		return pattern.matcher(val).matches();
	}

	public static boolean isPrice(String value) {
		value = value.strip();
		if (!value.contains(".")) {
			value = value.concat(".00");
		} else if (value.substring(value.lastIndexOf(".") + 1).length() == 1) {
			value = value.concat("0");
		}
		Pattern pattern1 = Pattern.compile("[0.]*");
		if (pattern1.matcher(value).matches()) {
			return false;
		}
		if (value.length() > 13) {
			return false;
		}
		// Pattern pattern = Pattern.compile("^\\d{1,10}.\\d{0,2}$");
		Pattern pattern = Pattern.compile("^(?!0*\\.0*$)\\d{1,10}(?:\\.\\d{0,2})?$");
		return pattern.matcher(value).matches();
	}

	public static void main(String[] args) {
		System.out.println(standardTextValidation(""));
	}
}
