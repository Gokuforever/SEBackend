package com.sorted.commons.beans;

import java.util.List;

import com.sorted.commons.helper.ReqBaseBean;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AddBulkProductReqBean extends ReqBaseBean {

	private String seller_id;
	private String category_id;
	private List<ProductReqBean> products;
}
