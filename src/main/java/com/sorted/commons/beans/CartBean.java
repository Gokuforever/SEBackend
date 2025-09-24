package com.sorted.commons.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartBean {

    private BigDecimal total_amount = BigDecimal.ZERO;
    private BigDecimal delivery_charge = BigDecimal.ZERO;
    private BigDecimal item_total = BigDecimal.ZERO;
    private BigDecimal item_total_mrp = BigDecimal.ZERO;
    private boolean is_free_delivery = false;
    private long total_count;
    private List<CartItems> cart_items;
    private boolean isStoreOperational = true;
    @JsonProperty("coupon_code")
    private String couponCode;
    @JsonProperty("discount_amount")
    private BigDecimal discountAmount;
}
