package com.sorted.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InputType {
	DROPDOWN(1), RADIO_BUTTON(2), INPUT(3), CHECK_BOX(4);

	private int id;
}