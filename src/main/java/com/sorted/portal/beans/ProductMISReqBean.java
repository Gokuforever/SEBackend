package com.sorted.portal.beans;

import java.util.List;

import com.sorted.portal.entity.mongo.Catagory_Master.SubCatagory;

import lombok.Data;

@Data
public class ProductMISReqBean {

	private String name;
	private String base_ctagory_code;
	private List<SubCatagory> sub_catagories;

}
