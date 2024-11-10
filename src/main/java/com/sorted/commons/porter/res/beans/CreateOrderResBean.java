package com.sorted.commons.porter.res.beans;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOrderResBean {

	private String request_id;
	private String order_id;
	private Long estimated_pickup_time;
	private EstimatedFareDetails estimated_fare_details;
	private String tracking_url;

	@Data
	@Builder
	public static class EstimatedFareDetails {
		private String currency;
		private Long minor_amount;
	}
}
