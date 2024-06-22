package com.sorted.commons.helper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sorted.commons.exceptions.CustomIllegalArgumentsException;

@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(CustomIllegalArgumentsException.class)
	public ResponseEntity<Object> handleCustomIllegalArgumentsException(CustomIllegalArgumentsException e) {
		HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
		String errMessage = e.getMessage() == null
				? "Illegal argument exception, please check your request parameters and body"
				: e.getMessage();
//		String userMessage = e.getUserMessage() == null ? Defaults.DEF_USER_ERR_MSG : e.getUserMessage();
		String userMessage = e.getUserMessage() == null ? errMessage : e.getUserMessage();

		SEResponse apiResponse = SEResponse.builder().status(status).responseCode(e.getMessageCode())
				.errorMessage(errMessage).userMessage(userMessage).build();
		return new ResponseEntity<Object>(apiResponse, new HttpHeaders(), status);
	}

}
