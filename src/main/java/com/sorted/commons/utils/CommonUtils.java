package com.sorted.commons.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import com.sorted.commons.helper.ReqBaseBean;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;

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
		return set.stream().toList();
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

	public static Long rupeeToPaise(@NonNull BigDecimal val) {
		val = val.setScale(2, RoundingMode.HALF_DOWN);
		return val.multiply(BigDecimal.valueOf(100)).longValue();
	}

	public static BigDecimal paiseToRupee(@NonNull Long val) {
		BigDecimal bigDecimal = new BigDecimal(val).divide(BigDecimal.valueOf(100));
		return bigDecimal.setScale(2, RoundingMode.HALF_DOWN);
	}

	public static String createCode(@NonNull String prefix) {
		long nanoseconds = CommonUtils.getNanoseconds();
		StringBuilder stringBuffer = new StringBuilder();
		stringBuffer.append(prefix);
		stringBuffer.append(nanoseconds);
		stringBuffer.append("-");
		stringBuffer.append(Year.now());
		return stringBuffer.toString();
	}

	public static <T extends ReqBaseBean> void extractHeaders(HttpServletRequest httpServletRequest, T bean) {
		try {
			String req_user_id = httpServletRequest.getHeader("req_user_id");
			String req_role_id = httpServletRequest.getHeader("req_role_id");
			if (!StringUtils.hasText(req_user_id) || !StringUtils.hasText(req_role_id)) {
				throw new CustomIllegalArgumentsException(ResponseCode.ACCESS_DENIED);
			}
			bean.setReq_user_id(req_user_id);
			bean.setReq_role_id(req_role_id);
		} catch (Exception e2) {
			throw new CustomIllegalArgumentsException(ResponseCode.ACCESS_DENIED);
		}
	}

	public static String toTitleCase(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}

		// Trim leading/trailing spaces and replace multiple spaces with a single space
		input = input.trim().replaceAll("\\s+", " ");

		String[] words = input.split(" "); // Split by single space now
		StringBuilder titleCased = new StringBuilder();

		for (String word : words) {
			if (word.length() > 0) {
				String firstLetter = word.substring(0, 1).toUpperCase(); // Capitalize first letter
				String remaining = word.substring(1).toLowerCase(); // Lowercase the rest
				titleCased.append(firstLetter).append(remaining).append(" ");
			}
		}

		// Remove the last extra space
		return titleCased.toString().trim();
	}

	public static boolean isImage(MultipartFile file) {
		// Check if the file is empty
		if (file.isEmpty()) {
			return false;
		}

		// Get the content type of the file
		String contentType = file.getContentType();
		if (contentType != null && contentType.startsWith("image/")) {
			return true;
		}

		// Alternatively, you can also check the file extension (optional)
		String fileName = file.getOriginalFilename();
		if (fileName != null) {
			String extension = getFileExtension(fileName);
			return isImageExtension(extension);
		}

		return false;
	}

	// Extract file extension
	private static String getFileExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex != -1) ? fileName.substring(dotIndex + 1) : "";
	}

	// Check if the extension belongs to an image type
	private static boolean isImageExtension(String extension) {
		// Add more image extensions as needed
		return extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
				|| extension.equalsIgnoreCase("png");
	}

	public static LocalDateTime convertEpochToLocalDateTime(long epochMillis) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
	}

	public static void main(String[] args) {
		System.out.println(convertEpochToLocalDateTime(1668081552000L));
	}
}
