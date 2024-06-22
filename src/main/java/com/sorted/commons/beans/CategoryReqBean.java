package com.sorted.commons.beans;

import java.util.List;

import com.sorted.commons.entity.mongo.Category_Master.SubCategory;

import lombok.Data;

@Data
public class CategoryReqBean {

	private String category_name;
	private List<SubCategory> sub_categories;

}
