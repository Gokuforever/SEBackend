package com.sorted.commons.beans;

import lombok.Builder;

@Builder
public class DeliveryRequestAttempts {

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
