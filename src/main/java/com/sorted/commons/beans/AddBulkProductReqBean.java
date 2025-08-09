package com.sorted.commons.beans;

import com.sorted.commons.helper.ReqBaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class AddBulkProductReqBean extends ReqBaseBean {

    private String seller_id;
    private List<ProductReqBean> products;
}
