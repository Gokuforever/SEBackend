package com.sorted.portal.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sorted.portal.enums.ResponseCode;
import com.sorted.portal.exceptions.CustomIllegalArgumentsException;

public class PasswordValidatorUtils {

	public static void validatePassword(String password) {

		Pattern lowerletter = Pattern.compile("[a-z]");
		Pattern upperletter = Pattern.compile("[A-Z]");
		Pattern digit = Pattern.compile("\\d");
		Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
		Pattern eight = Pattern.compile(".{8}");

		Matcher hasLowerLetter = lowerletter.matcher(password);
		Matcher hasUpperLetter = upperletter.matcher(password);
		Matcher hasDigit = digit.matcher(password);
		Matcher hasSpecial = special.matcher(password);
		Matcher hasEight = eight.matcher(password);

		if (!hasLowerLetter.find()) {
			throw new CustomIllegalArgumentsException(ResponseCode.PASS_VALIDATION_FAILURE_1);
		}

		if (!hasUpperLetter.find()) {
			throw new CustomIllegalArgumentsException(ResponseCode.PASS_VALIDATION_FAILURE_2);
		}

		if (!hasDigit.find()) {
			throw new CustomIllegalArgumentsException(ResponseCode.PASS_VALIDATION_FAILURE_3);
		}

		if (!hasSpecial.find()) {
			throw new CustomIllegalArgumentsException(ResponseCode.PASS_VALIDATION_FAILURE_4);
		}

		if (!hasEight.find()) {
			throw new CustomIllegalArgumentsException(ResponseCode.PASS_VALIDATION_FAILURE_5);
		}

	}

}
