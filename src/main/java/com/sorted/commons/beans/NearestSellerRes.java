package com.sorted.commons.beans;

import java.io.Serial;
import java.io.Serializable;

import com.sorted.commons.porter.res.beans.GetQuoteResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NearestSellerRes implements Serializable{

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	private String seller_id;
	private GetQuoteResponse response;
	private boolean is_operational;

}
