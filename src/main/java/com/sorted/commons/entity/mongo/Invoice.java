package com.sorted.commons.entity.mongo;

import com.sorted.commons.beans.BuyerInfo;
import com.sorted.commons.beans.InvoiceItem;
import com.sorted.commons.beans.PaymentInfo;
import com.sorted.commons.beans.SellerInfo;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Builder
@Document(collection = "invoice")
public class Invoice extends BaseMongoEntity<String>{

    @Field("order_id")
    private String orderId;
    @Field("invoice_id")
    private String invoiceId;
    @Field("invoice_date")
    private LocalDateTime invoiceDate;
    private SellerInfo seller;
    private BuyerInfo buyer;
    private List<InvoiceItem> items;
    @Field("total_amount")
    private BigDecimal totalAmount;
    @Field("total_gst_amount")
    private BigDecimal totalGstAmount;
    @Field("total_net_amount")
    private BigDecimal totalNetAmount;
    @Field("total_amount_in_words")
    private String totalAmountInWords;
    @Field("payment_info")
    private PaymentInfo paymentInfo;

}
