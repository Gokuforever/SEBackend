package com.sorted.commons.beans;

import java.util.List;
import java.util.Map;

import com.sorted.commons.entity.mongo.Category_Master;
import com.sorted.commons.helper.ReqBaseBean;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductReqBean extends ReqBaseBean {
	private String seller_id;
	private String category_id;
	private Map<String, List<String>> sub_categories;
	private String name;
	private String selling_price;
	private String mrp;
	private String description;
	private String quantity;
	private Category_Master selected_category;
	private List<Media> media;
	private String varient_name;
	private String varient_mapping_id;

}
