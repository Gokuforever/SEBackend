package com.sorted.portal.helper;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorted.portal.enums.ResponseCode;
import com.sorted.portal.exceptions.CustomIllegalArgumentsException;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Request implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private String source;
	private String requestDataType;
	private Object requestData;
	private Filter filterData;
	private Object identifier;

	@JsonIgnore
	public <K> K getGenericRequestDataObject(@NonNull Class<K> clazz) {
		if (this.getRequestData() != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				return mapper.convertValue(this.getRequestData(), clazz);
			} catch (IllegalArgumentException e) {
				throw new CustomIllegalArgumentsException(ResponseCode.INVALID_REQ);
			}
		} else {
			String err = "RequestData is null";
			throw new CustomIllegalArgumentsException(err);
		}
	}
}