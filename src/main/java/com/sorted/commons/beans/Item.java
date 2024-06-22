package com.sorted.commons.beans;

import java.math.BigDecimal;

import com.sorted.commons.enums.CartItemsStatus;

import lombok.Data;

@Data
public class Item {
	private String product_id;
	private String product_code;
	private Long quantity;
	private BigDecimal price;
	private BigDecimal sub_total;
	private Integer status = CartItemsStatus.ADDED.getId();
}
