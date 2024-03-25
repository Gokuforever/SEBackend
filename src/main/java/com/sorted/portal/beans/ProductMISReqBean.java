package com.sorted.portal.beans;

import java.util.List;

import com.sorted.portal.entity.mongo.Category_Master.SubCategory;

import lombok.Data;

@Data
public class ProductMISReqBean {

	private String name;
	private String base_category_code;
	private List<SubCategory> sub_categories;

}
