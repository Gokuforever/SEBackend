package com.sorted.portal.beans;

import java.util.List;

import com.sorted.portal.entity.mongo.Catagory_Master.SubCatagory;

import lombok.Data;

@Data
public class CatagoryReqBean {

	private String catagory_name;
	private List<SubCatagory> sub_catagories;
	
}
