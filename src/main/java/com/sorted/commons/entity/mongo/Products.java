package com.sorted.commons.entity.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sorted.commons.beans.Media;
import com.sorted.commons.beans.SelectedSubCatagories;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "products")
public class Products extends BaseMongoEntity<String> {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private String product_code;
	private String name;
	private Long cost_price;
	private Long selling_price;
	private Long mrp;
	private String discount_tag;
	private String description;
	private Long quantity;
	private String category_id;
	private String category_code;
	private String seller_id;
	private String seller_code;
	private String varient_mapping_id;
	private List<SelectedSubCatagories> selected_sub_catagories;
	private List<Media> media;

}
