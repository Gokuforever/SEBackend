package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CashfreeOrderStatus {
	ACTIVE(1), PAID(2), EXPIRED(3), TERMINATED(4), TERMINATION_REQUESTED(5);

	private int id;
	// @formatter:off
	// - `ACTIVE`: Order does not have a successful transaction yet
	// - `PAID`: Order is PAID with one successful transaction
	// -`EXPIRED`: Order was not PAID and not it has expired. No transaction can be initiated for an EXPIRED order.
	// -`TERMINATED`: Order terminated
	// -`TERMINATION_REQUESTED`: Order termination requested"
	// @formatter:on

}
