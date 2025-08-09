package com.sorted.commons.beans;

import com.sorted.commons.entity.mongo.Category_Master.SubCategory;
import lombok.Data;

import java.util.List;

@Data
public class CategoryReqBean {

	private String category_name;
	private List<SubCategory> sub_categories;

}
