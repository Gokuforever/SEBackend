package com.sorted.commons.helper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;

@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(CustomIllegalArgumentsException.class)
	public ResponseEntity<Object> handleCustomIllegalArgumentsException(CustomIllegalArgumentsException e) {
		HttpStatus status = null == e.getHttpStatus() ? HttpStatus.NOT_ACCEPTABLE : e.getHttpStatus();
		if (e.getResponseCode() == ResponseCode.ACCESS_DENIED) {
			status = HttpStatus.UNAUTHORIZED;
		}
		String errMessage = e.getMessage() == null
				? "Illegal argument exception, please check your request parameters and body"
				: e.getMessage();
		String userMessage = e.getUserMessage() == null ? errMessage : e.getUserMessage();

		SEResponse apiResponse = SEResponse.builder().status(status).responseCode(e.getMessageCode())
				.errorMessage(errMessage).userMessage(userMessage).build();
		return new ResponseEntity<Object>(apiResponse, new HttpHeaders(), status);
	}

}
