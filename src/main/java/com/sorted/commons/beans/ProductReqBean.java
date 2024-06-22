package com.sorted.commons.beans;

import com.sorted.commons.entity.mongo.Category_Master;

import lombok.Data;

@Data
public class ProductReqBean {
	private String name;
	private String price;
	private String description;
	private String quantity;
	private Category_Master selected_category;

}
