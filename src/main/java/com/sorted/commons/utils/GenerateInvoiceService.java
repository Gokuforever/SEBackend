package com.sorted.commons.utils;

import com.sorted.commons.beans.*;
import com.sorted.commons.entity.mongo.*;
import com.sorted.commons.entity.service.InvoiceService;
import com.sorted.commons.entity.service.Order_Item_Service;
import com.sorted.commons.entity.service.Seller_Service;
import com.sorted.commons.entity.service.Users_Service;
import com.sorted.commons.enums.DocumentType;
import com.sorted.commons.enums.OrderStatus;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import com.sorted.commons.helper.AggregationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GenerateInvoiceService {

    private final Users_Service usersService;
    private final Seller_Service sellerService;
    private final Order_Item_Service orderItemService;
    private final AwsS3Service awsS3Service;
    private final InvoiceService invoiceService;


    private String generateInvoice(Order_Details orderDetails) throws IOException {
        if (orderDetails.getStatus() != OrderStatus.DELIVERED) {
            throw new CustomIllegalArgumentsException(ResponseCode.INVALID_ORDER_STATUS);
        }

        UsersBean buyer = usersService.validateAndGetUserInfo(orderDetails.getUser_id());
        Seller seller = sellerService.findById(orderDetails.getSeller_id()).orElseThrow(() -> new CustomIllegalArgumentsException(ResponseCode.SELLER_NOT_FOUND));

        List<InvoiceItem> invoiceItems = new ArrayList<>();
        AggregationFilter.SEFilter filterOI = new AggregationFilter.SEFilter(AggregationFilter.SEFilterType.AND);
        filterOI.addClause(AggregationFilter.WhereClause.eq(Order_Item.Fields.order_id, orderDetails.getId()));
        filterOI.addClause(AggregationFilter.WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
        List<Order_Item> orderItems = orderItemService.repoFind(filterOI);
        if (!CollectionUtils.isEmpty(orderItems)) {
            for (Order_Item item : orderItems) {
                invoiceItems.add(InvoiceItem.builder()
                        .productId(item.getProduct_code())
                        .productName(item.getProduct_name())
                        .hsnCode("")
                        .quantity(item.getQuantity())
                        .unitPrice(CommonUtils.paiseToRupee(item.getSelling_price()))
                        .totalPrice(CommonUtils.paiseToRupee(item.getTotal_cost()))
                        .build());
            }
        }


        Invoice invoice = Invoice.builder()
                .invoiceId("INV" + orderDetails.getCode() + CommonUtils.generateFixedLengthRandomNumber(2))
                .invoiceDate(LocalDateTime.now())
                .seller(SellerInfo.builder()
                        .name("Studeaze Partner Store" + " #" + seller.getStore_no())
                        .address(orderDetails.getPickup_address().getFullAddress())
                        .phoneNo(seller.getSpoc_details().stream().filter(Spoc_Details::isPrimary).findFirst().get().getMobile_no())
                        .sellerId(seller.getCode())
                        .gstNo(seller.getGstin())
                        .build())
                .buyer(BuyerInfo.builder()
                        .email(buyer.getEmail_id())
                        .name(buyer.getFirst_name() + " " + buyer.getLast_name())
                        .address(orderDetails.getDelivery_address().getFullAddress())
                        .build())
                .items(invoiceItems)
                .totalAmount(CommonUtils.paiseToRupee(orderDetails.getTotal_amount()))
                .totalGstAmount(BigDecimal.ZERO)
                .totalNetAmount(CommonUtils.paiseToRupee(orderDetails.getTotal_amount()))
                .totalAmountInWords(IndianCurrencyConverter.convertToWords(CommonUtils.paiseToRupee(orderDetails.getTotal_amount()).doubleValue()))
                .paymentInfo(PaymentInfo.builder()
                        .paymentMethod(orderDetails.getPayment_mode())
                        .transactionId(orderDetails.getTransaction_id())
                        .paymentDate(orderDetails.getOrder_status_history().stream()
                                .filter(e -> e.getStatus() == OrderStatus.TRANSACTION_PROCESSED)
                                .findFirst()
                                .map(Order_Status_History::getModification_date)
                                .orElseThrow(() -> new CustomIllegalArgumentsException(ResponseCode.ERR_0001)))
                        .build())
                .build();

        invoice = invoiceService.create(invoice, buyer.getId());

        byte[] pdfBytes = InvoicePdfGenerator.generateInvoicePdf(invoice);
        File_Upload_Details fileUploadDetails = awsS3Service.uploadPdf(pdfBytes, invoice.getInvoiceId() + ".pdf", buyer, DocumentType.INVOICE);
        return fileUploadDetails.getFile_url();
    }
}
