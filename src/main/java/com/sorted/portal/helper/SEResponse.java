package com.sorted.portal.helper;

import java.io.Serializable;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.sorted.portal.enums.ResponseCode;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class SEResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HttpStatus status;
	String userMessage;
	String responseDataType;
	String responseData;
	String responseCode;
	String errorMessage;
	String sysErrorMessage;

	@JsonIgnore
	public static SEResponse getEmptySuccessResponse(ResponseCode message) {
		SEResponse apiResponse;
		apiResponse = SEResponse.builder().status(HttpStatus.OK).userMessage(message.getUserMessage()).responseData("")
				.responseDataType("").build();

		return apiResponse;
	}

	@JsonIgnore
	public static SEResponse getBasicSuccessResponseObject(@NonNull Object e, ResponseCode message) {
		if (e instanceof Iterable) {
			String err = "Don't Use list in this constructor";

			return getGenericFailureResponse(err);
		}

		return SEResponse.builder().status(HttpStatus.OK).userMessage(message.getUserMessage())
				.responseCode(message.getCode()).errorMessage(message.getErrorMessage())
				.responseData(new Gson().toJson(e)).responseDataType(e.getClass().getSimpleName()).build();
	}

	@JsonIgnore
	public static SEResponse getBasicSuccessResponseList(@NonNull List<?> list, ResponseCode message) {

		return SEResponse.builder().status(HttpStatus.OK).userMessage(message.getUserMessage())
				.responseCode(message.getCode()).errorMessage(message.getErrorMessage())
				.responseData(new Gson().toJson(list)).responseDataType(list.getClass().getSimpleName()).build();
	}

	@JsonIgnore
	public static SEResponse getBadRequestFailureResponse(ResponseCode error) {
		String inErrorMessage = "Bad request";
		return SEResponse.builder().status(HttpStatus.BAD_REQUEST).errorMessage(inErrorMessage)
				.userMessage(error.getUserMessage()).sysErrorMessage(error.getErrorMessage())
				.responseCode(error.getCode()).build();
	}

	@JsonIgnore
	public static SEResponse getGenericFailureResponse(String inSysErrorMessage) {
		String inErrorMessage = "Something went wrong";
		SEResponse apiResponse = SEResponse.builder().status(HttpStatus.SERVICE_UNAVAILABLE)
				.errorMessage(inErrorMessage).sysErrorMessage(inSysErrorMessage).build();
		return apiResponse;
	}

}
