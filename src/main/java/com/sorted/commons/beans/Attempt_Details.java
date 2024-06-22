package com.sorted.commons.beans;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Attempt_Details {

	private String otp;
	private LocalDateTime attempt_date;
	private String ip_address;
}
