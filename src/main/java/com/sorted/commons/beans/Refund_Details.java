package com.sorted.commons.beans;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class Refund_Details {
	private LocalDateTime return_date;
	private String return_reason;
	private Long refund_amount;
	private String return_status;
}
