package com.sorted.commons.porter.res.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class FetchOrderRes {

	private String order_id;
	private Status status;
	private PartnerInfo partner_info;
	private OrderTimings order_timings;
	private FareDetails fare_details;

	@Getter
	@AllArgsConstructor
	public enum Status {
		open, accepted, live, ended, cancelled
	}

	@Data
	@Builder
	public static class PartnerInfo {
		private String name;
		private String vehicle_number;
		private String vehicle_type;
		private MobileNo mobile;
		private MobileNo partner_secondary_mobile;
		private Location location;

	}

	@Data
	@Builder
	public static class Location {
		private String lat;
		private String lng;
	}

	@Data
	@Builder
	public static class MobileNo {
		private String country_code;
		private String mobile_number;
	}

	@Data
	@Builder
	public static class OrderTimings {
		private Long pickup_time;
		private Long order_accepted_time;
		private Long order_started_time;
		private Long order_ended_time;
	}

	@Data
	@Builder
	public static class FareDetails {
		private FareAmountDetails estimated_fare_details;
		private FareAmountDetails actual_fare_details;

		@Data
		@Builder
		public static class FareAmountDetails {
			private String currency;
			private Long minor_amount;
		}

	}
}
