package com.sorted.commons.beans;

import lombok.Builder;

import java.io.Serializable;

@Builder
public class DeliveryRequestAttempts implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	private String message;
	private int count;
	private int response_code;

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public int getCount() {
		return count;
	}

	public int getResponse_code() {
		return response_code;
	}
}
