package com.sorted.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sorted.commons.beans.CartBean;
import com.sorted.commons.beans.CartItems;
import com.sorted.commons.beans.Item;
import com.sorted.commons.beans.Media;
import com.sorted.commons.entity.mongo.BaseMongoEntity;
import com.sorted.commons.entity.mongo.Cart;
import com.sorted.commons.entity.mongo.Products;
import com.sorted.commons.entity.mongo.Seller;
import com.sorted.commons.entity.service.*;
import com.sorted.commons.enums.All_Status;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import com.sorted.commons.helper.AggregationFilter;
import com.sorted.commons.porter.res.beans.GetQuoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartUtility {

    private final Cart_Service cart_Service;
    private final ProductService productService;
    private final EstimateDeliveryService estimateDeliveryService;
    private final StoreActivityService storeActivityService;
    private final Seller_Service sellerService;
    private final DemandingPincodeService demandingPincodeService;
    private final Address_Service addressService;
    private final CouponUtility couponUtility;

    @Value("${se.minimum-cart-value.in-paise:10000}")
    private long minCartValueInPaise;

    @Value("${se.fixed-delivery-charge.in-paise:5900}")
    private long fixedDeliveryCharge;

    public CartBean getCartBean(Cart cart) throws JsonProcessingException {
        return this.getCartBean(cart, null, null);
    }

    public CartBean getCartBean(Cart cart, String address_id, String customerName) throws JsonProcessingException {
        CartBean cartBean = new CartBean();
        List<CartItems> cartItems = new ArrayList<>();
        List<Long> total_price_in_paise = new ArrayList<>();
        List<Long> total_cart_items = new ArrayList<>();
        List<Item> cart_items = cart.getCart_items();
        String seller_id = null;
        if (!CollectionUtils.isEmpty(cart_items)) {
            List<String> product_ids = cart_items.stream().map(Item::getProduct_id).toList();
            AggregationFilter.SEFilter filterP = new AggregationFilter.SEFilter(AggregationFilter.SEFilterType.AND);
            filterP.addClause(AggregationFilter.WhereClause.in(BaseMongoEntity.Fields.id, product_ids));
//			filterP.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

            List<Products> listP = productService.repoFind(filterP);
            if (!CollectionUtils.isEmpty(listP)) {
                seller_id = listP.get(0).getSeller_id();
                Map<String, Products> mapP = listP.stream().collect(Collectors.toMap(BaseMongoEntity::getId, p -> p));

                cart_items.forEach(e -> {
                    if (mapP.containsKey(e.getProduct_id())) {
                        CartItems items = new CartItems();
                        Products products = mapP.get(e.getProduct_id());
                        items.setProduct_name(products.getName());
                        items.setProduct_code(e.getProduct_code());
                        items.setProduct_id(e.getProduct_id());
                        items.setQuantity(e.getQuantity());
                        items.setSelling_price(CommonUtils.paiseToRupee(products.getSelling_price()));
                        items.setSecure_item(e.is_secure());
                        if (products.isDeleted()) {
                            items.setCurrent_status(All_Status.ProductCurrentStatus.CURRENTLY_UNAVAILABLE.getStatus_id());
                        } else if (products.getQuantity().compareTo(e.getQuantity()) >= 0) {
                            total_price_in_paise.add(products.getSelling_price() * items.getQuantity());
                            items.setCurrent_status(All_Status.ProductCurrentStatus.IN_STOCK.getStatus_id());
                            total_cart_items.add(items.getQuantity());
                        } else {
                            items.setCurrent_status(All_Status.ProductCurrentStatus.OUT_OF_STOCK.getStatus_id());
                        }
                        List<Media> media = products.getMedia();
                        if (!CollectionUtils.isEmpty(media)) {
                            Optional<Media> findFirst = media.stream().filter(m -> m.getOrder() == 0).findFirst();
                            findFirst.ifPresent(value -> items.setCdn_url(value.getCdn_url()));
                        }
                        cartItems.add(items);
                    }
                });
            }
        }
        long summed = total_price_in_paise.stream().mapToLong(Long::longValue).sum();
        long total_items = total_cart_items.stream().mapToLong(Long::longValue).sum();
        boolean addressPresent = StringUtils.hasText(address_id);
        if (addressPresent && summed > 0) {
            Seller seller = sellerService.findById(seller_id).orElseThrow(() -> new CustomIllegalArgumentsException(ResponseCode.SELLER_NOT_FOUND));
            GetQuoteResponse quote = estimateDeliveryService.getEstimateDeliveryAmount(address_id, seller.getAddress_id(), customerName);
            if (quote != null) {
                cart.setDelivery_charges(fixedDeliveryCharge);
                cart_Service.update(cart.getId(), cart, cart.getModified_by());
            } else {
                addressService.findById(address_id).ifPresent(address ->
                        demandingPincodeService.storeDemandingPincode(address.getPincode(), cart.getUser_id()));
            }
        }
        boolean freeDelivery = minCartValueInPaise <= summed;
        cartBean.setItem_total(CommonUtils.paiseToRupee(summed));
        cartBean.setTotal_count(total_items);
        cartBean.setCart_items(cartItems);
        cartBean.setDelivery_charge(total_items > 0 ? CommonUtils.paiseToRupee(fixedDeliveryCharge) : BigDecimal.ZERO);
        cartBean.setTotal_amount(summed > 0 ? freeDelivery ? CommonUtils.paiseToRupee(summed) : CommonUtils.paiseToRupee(summed + fixedDeliveryCharge) : BigDecimal.ZERO);
        cartBean.set_free_delivery(freeDelivery);
        cartBean.setStoreOperational(storeActivityService.isStoreOperational(seller_id));

        cartBean.setDiscountAmount(BigDecimal.ZERO);
        if (StringUtils.hasText(cart.getCouponCode()) && summed > 0) {
            boolean valid = couponUtility.validateCouponByCode(cart.getCouponCode(), cartBean);
            if (valid) {
                Long discountAmount = couponUtility.calculateDiscountAmount(cart.getCouponCode(), cartBean);
                cartBean.setDiscountAmount(CommonUtils.paiseToRupee(discountAmount));
            } else {
                cart.setCouponCode(null);
                cartBean.setDiscountAmount(BigDecimal.ZERO);
            }

//            Long discountAmount = couponUtility.validateCouponAndGetDiscount(cartBean, coupon, usersBean.getId());
//            cart.setCouponCode(coupon.getCode());
//            cartBean.setCouponCode(coupon.getCode());
//            cartBean.setDiscountAmount(CommonUtils.paiseToRupee(discountAmount));
        }

        return cartBean;
    }

}
