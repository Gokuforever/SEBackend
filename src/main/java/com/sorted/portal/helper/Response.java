package com.sorted.portal.helper;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sorted.portal.enums.ResponseCode;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response implements Serializable {

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
	public static Response getEmptySuccessResponse(ResponseCode message) {
		Response apiResponse;
		apiResponse = Response.builder().status(HttpStatus.OK).userMessage(message.getUserMessage()).responseData("")
				.responseDataType("").build();

		return apiResponse;
	}

	@JsonIgnore
	public static Response getBadRequestFailureResponse(ResponseCode error) {
		String inErrorMessage = "Bad request";
		Response apiResponse = Response.builder().status(HttpStatus.BAD_REQUEST).errorMessage(inErrorMessage)
				.userMessage(error.getUserMessage()).sysErrorMessage(error.getErrorMessage())
				.responseCode(error.getCode()).build();
		return apiResponse;
	}

}
