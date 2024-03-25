package com.sorted.portal.beans;

import java.util.List;

import com.sorted.portal.entity.mongo.Category_Master.SubCategory;

import lombok.Data;

@Data
public class CategoryReqBean {

	private String category_name;
	private List<SubCategory> sub_categories;

}
