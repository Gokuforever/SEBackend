package com.sorted.commons.beans;

import java.time.LocalDateTime;

import com.sorted.commons.enums.OrderStatus;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class Order_Status_History {
	private OrderStatus status;
	private LocalDateTime modification_date;
	private String modified_by;
}
