package com.sorted.portal.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartCUDReqBean extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String product_id;
	private String quantity;

}
