package com.sorted.commons.porter.res.beans;

import java.time.LocalDateTime;

import com.sorted.commons.porter.res.beans.FetchOrderRes.FareDetails.FareAmountDetails;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOrderResBean {

	private String request_id;
	private String order_id;
	private LocalDateTime estimated_pickup_time;
	private FareAmountDetails estimated_fare_details;
	private String tracking_url;

}
