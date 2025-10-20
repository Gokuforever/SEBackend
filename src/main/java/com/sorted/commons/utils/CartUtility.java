package com.sorted.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sorted.commons.beans.*;
import com.sorted.commons.entity.mongo.*;
import com.sorted.commons.entity.service.*;
import com.sorted.commons.enums.All_Status;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import com.sorted.commons.helper.AggregationFilter.SEFilter;
import com.sorted.commons.helper.AggregationFilter.SEFilterType;
import com.sorted.commons.helper.AggregationFilter.WhereClause;
import com.sorted.commons.porter.res.beans.GetQuoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
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
    private final ComboUtility comboUtility;

    @Value("${se.fixed-delivery-charge.in-paise:4000}")
    private long fixedDeliveryFee;

    @Value("${se.minimum-cart-value.in-paise:39900}")
    private long minCartValueInPaise;

    @Value("${se.small-cart-fee.in-paise:1000}")
    private long fixedSmallCartFee;

    @Value("${se.handling-fee.in-paise:900}")
    private long fixedHandlingFee;

    public CartBeanV2 getCartBeanV2(Cart cart) throws JsonProcessingException {
        return this.getCartBeanV2(cart, null, null);
    }

    public CartBeanV2 getCartBeanV2(Cart cart, String addressId, String customerName) throws JsonProcessingException {


        List<Item> itemList = cart.getCart_items();
        BigDecimal zero = BigDecimal.ZERO;
        if (CollectionUtils.isEmpty(itemList)) {
            return CartBeanV2.builder()
                    .cartItems(new ArrayList<>())
                    .billingSummary(BillingSummary.builder()
                            .couponCode(null)
                            .toPay(zero)
                            .savings(zero)
                            .deliveryFee(zero)
                            .smallCartFee(zero)
                            .handlingFee(zero)
                            .totalMrp(zero)
                            .totalSellingPrice(zero)
                            .couponDiscount(zero)
                            .deliveryFee(zero)
                            .handlingFee(zero)
                            .smallCartFee(zero)
                            .actualDeliveryFee(zero)
                            .actualSmallCartFee(zero)
                            .actualHandlingFee(zero)
                            .build())
                    .isFreeDelivery(false)
                    .isStoreOperational(false)
                    .totalItemCount(0L)
                    .build();
        }
        List<CartItems> cartItems = new ArrayList<>();
        BigDecimal minCartValue = CommonUtils.paiseToRupee(minCartValueInPaise);
        String couponCode = null;
        BigDecimal toPay = zero;
        BigDecimal savings = zero;
        BigDecimal deliveryFee = CommonUtils.paiseToRupee(fixedDeliveryFee);
        BigDecimal smallCartFee = CommonUtils.paiseToRupee(fixedSmallCartFee);
        BigDecimal handlingFee = CommonUtils.paiseToRupee(fixedHandlingFee);
        BigDecimal totalMrp = zero;
        BigDecimal totalSellingPrice = zero;
        BigDecimal couponDiscount = zero;
        BigDecimal actualDeliveryFee = CommonUtils.paiseToRupee(fixedDeliveryFee);
        BigDecimal actualSmallCartFee = CommonUtils.paiseToRupee(fixedSmallCartFee);
        BigDecimal actualHandlingFee = CommonUtils.paiseToRupee(fixedHandlingFee);
        long totalItemCount = 0;
        boolean isFreeDelivery = false;
        boolean isStoreOperational;
        String sellerId = null;

        List<String> comboIds = itemList.stream().filter(Item::isCombo).map(Item::getProduct_id).toList();

        if (!CollectionUtils.isEmpty(comboIds)) {
            List<Combo> combos = comboUtility.getActiveCombos(comboIds);
            if (!CollectionUtils.isEmpty(combos)) {
                Map<String, Combo> comboMap = combos.stream().collect(Collectors.toMap(BaseMongoEntity::getId, combo -> combo));
                for (Item i : itemList) {
                    if (!i.isCombo()) {
                        continue;
                    }
                    Combo combo = comboMap.getOrDefault(i.getProduct_id(), null);
                    if (combo == null) {
                        continue;
                    }
                    CartItems items = new CartItems();
                    items.setProduct_name(combo.getName());
                    items.setProduct_code(i.getProduct_code());
                    items.setProduct_id(i.getProduct_id());
                    items.setQuantity(i.getQuantity());
                    items.setSelling_price(CommonUtils.paiseToRupee(combo.getSelling_price()));
                    items.setSecure_item(i.is_secure());
                    items.setMrp(CommonUtils.paiseToRupee(combo.getMrp()));
                    items.setCurrent_status(All_Status.ProductCurrentStatus.IN_STOCK.getStatus_id());
                    items.set_combo(true);
                    items.setCdn_url(!CollectionUtils.isEmpty(combo.getMedia()) ? combo.getMedia().get(0).getCdn_url() : "");
                    cartItems.add(items);
                    totalItemCount += items.getQuantity();
                    totalSellingPrice = totalSellingPrice.add(items.getSelling_price());
                    totalMrp = totalMrp.add(items.getMrp());
                }
            }
        }

        List<String> productIds = itemList.stream().filter(e -> !e.isCombo()).map(Item::getProduct_id).distinct().toList();

        if (!CollectionUtils.isEmpty(productIds)) {

            SEFilter filterP = new SEFilter(SEFilterType.AND);
            filterP.addClause(WhereClause.in(BaseMongoEntity.Fields.id, productIds));

            List<Products> listP = productService.repoFind(filterP);
            if (!CollectionUtils.isEmpty(listP)) {
                sellerId = listP.get(0).getSeller_id();
            }
            for (Item i : itemList) {
                if (i.isCombo()) {
                    continue;
                }
                Map<String, Products> productMap = listP.stream().collect(Collectors.toMap(BaseMongoEntity::getId, p -> p));
                Products products = productMap.get(i.getProduct_id());

                CartItems items = new CartItems();

                items.setProduct_name(products.getName());
                items.setProduct_code(i.getProduct_code());
                items.setProduct_id(i.getProduct_id());
                items.setQuantity(i.getQuantity());
                items.setSelling_price(CommonUtils.paiseToRupee(products.getSelling_price()));
                items.setSecure_item(i.is_secure());
                items.setMrp(CommonUtils.paiseToRupee(products.getMrp()));
                items.set_combo(false);
                if (products.isDeleted()) {
                    items.setCurrent_status(All_Status.ProductCurrentStatus.CURRENTLY_UNAVAILABLE.getStatus_id());
                } else if (products.getQuantity().compareTo(i.getQuantity()) >= 0) {
                    long sellingPriceInPaise = products.getSelling_price() * items.getQuantity();
                    totalSellingPrice = totalSellingPrice.add(CommonUtils.paiseToRupee(sellingPriceInPaise));
                    long mrpInPaise = products.getMrp() * items.getQuantity();
                    totalMrp = totalMrp.add(CommonUtils.paiseToRupee(mrpInPaise));
                    items.setCurrent_status(All_Status.ProductCurrentStatus.IN_STOCK.getStatus_id());
                    totalItemCount += items.getQuantity();
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
        }


        boolean addressPresent = StringUtils.hasText(addressId);
        if (addressPresent && totalSellingPrice.compareTo(zero) > 0) {
            Seller seller = sellerService.findById(sellerId).orElseThrow(() -> new CustomIllegalArgumentsException(ResponseCode.SELLER_NOT_FOUND));
            GetQuoteResponse quote = estimateDeliveryService.getEstimateDeliveryAmount(addressId, seller.getAddress_id(), customerName);
            if (quote != null) {
                cart.setDelivery_charges(fixedDeliveryFee);
                cart.setSmall_cart_fee(fixedSmallCartFee);
                cart.setHandling_charges(fixedHandlingFee);

                cart_Service.update(cart.getId(), cart, cart.getModified_by());
            } else {
                addressService.findById(addressId).ifPresent(address ->
                        demandingPincodeService.storeDemandingPincode(address.getPincode(), cart.getUser_id()));
            }
        }
        final BigDecimal sellingPrice = totalSellingPrice;
        BigDecimal actualSellingPrice = totalSellingPrice;
        boolean isNotSmallCart = totalSellingPrice.compareTo(minCartValue) > 0;
        if (!isNotSmallCart) {
            actualSellingPrice = totalSellingPrice.add(deliveryFee).add(smallCartFee).add(handlingFee);
        }

        boolean freeShippingIsIncludedInCoupon = false;
        if (StringUtils.hasText(cart.getCouponCode()) && totalSellingPrice.compareTo(zero) > 0) {
            CouponCodeInfo couponCodeInfo = couponUtility.validateCouponByCodeForCart(cart.getCouponCode(), CommonUtils.rupeeToPaise(actualSellingPrice), isNotSmallCart, cart.getUser_id());
            if (couponCodeInfo.isValid()) {
                freeShippingIsIncludedInCoupon = couponCodeInfo.isFreeDelivery();
                isFreeDelivery = couponCodeInfo.isFreeDelivery();
                couponCode = cart.getCouponCode();
                couponDiscount = CommonUtils.paiseToRupee(couponCodeInfo.discountAmount());
                actualSellingPrice = actualSellingPrice.subtract(couponDiscount);
            }
            savings = savings.add(CommonUtils.paiseToRupee(couponCodeInfo.discountAmount()));
        }

        if (!isFreeDelivery) {
            if (totalSellingPrice.compareTo(minCartValue) > 0) {
                isFreeDelivery = true;
            }
        }

        if (isFreeDelivery) {
            actualDeliveryFee = zero;
            actualSmallCartFee = zero;
            actualHandlingFee = zero;
        }
        if (isFreeDelivery && !freeShippingIsIncludedInCoupon) {
            savings = savings.add(deliveryFee);
            savings = savings.add(smallCartFee);
            savings = savings.add(handlingFee);
        }

        BigDecimal difference = totalMrp.subtract(sellingPrice);

        savings = savings.add(difference);

        toPay = actualDeliveryFee.add(actualSmallCartFee).add(actualHandlingFee).add(totalSellingPrice);

        isStoreOperational = storeActivityService.isStoreOperational(sellerId);

        cartItems = cartItems.stream().sorted(Comparator.comparing(CartItems::getProduct_id)).toList();

        return CartBeanV2.builder()
                .cartItems(cartItems)
                .billingSummary(BillingSummary.builder()
                        .couponCode(couponCode)
                        .toPay(toPay)
                        .savings(savings)
                        .deliveryFee(deliveryFee)
                        .smallCartFee(smallCartFee)
                        .handlingFee(handlingFee)
                        .totalMrp(totalMrp)
                        .totalSellingPrice(sellingPrice)
                        .couponDiscount(couponDiscount)
                        .deliveryFee(CommonUtils.paiseToRupee(fixedDeliveryFee))
                        .handlingFee(CommonUtils.paiseToRupee(fixedHandlingFee))
                        .smallCartFee(CommonUtils.paiseToRupee(fixedSmallCartFee))
                        .actualDeliveryFee(actualDeliveryFee)
                        .actualSmallCartFee(actualSmallCartFee)
                        .actualHandlingFee(actualHandlingFee)
                        .build())
                .isFreeDelivery(isFreeDelivery)
                .isStoreOperational(isStoreOperational)
                .totalItemCount(totalItemCount)
                .build();
    }

    public CartBean getCartBean(Cart cart) throws JsonProcessingException {
        return this.getCartBean(cart, null, null);
    }

    public CartBean getCartBean(Cart cart, String address_id, String customerName) throws JsonProcessingException {
        CartBean cartBean = new CartBean();
        List<CartItems> cartItems = new ArrayList<>();
        List<Long> total_price_in_paise = new ArrayList<>();
        List<Long> total_mrp_in_paise = new ArrayList<>();
        List<Long> total_cart_items = new ArrayList<>();
        List<Item> cart_items = cart.getCart_items();
        String seller_id = null;
        if (!CollectionUtils.isEmpty(cart_items)) {
            List<String> product_ids = cart_items.stream().map(Item::getProduct_id).toList();
            SEFilter filterP = new SEFilter(SEFilterType.AND);
            filterP.addClause(WhereClause.in(BaseMongoEntity.Fields.id, product_ids));
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
                            total_mrp_in_paise.add(products.getMrp() * items.getQuantity());
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
        long summedMRP = total_mrp_in_paise.stream().mapToLong(Long::longValue).sum();
        long total_items = total_cart_items.stream().mapToLong(Long::longValue).sum();
        boolean addressPresent = StringUtils.hasText(address_id);
        if (addressPresent && summed > 0) {
            Seller seller = sellerService.findById(seller_id).orElseThrow(() -> new CustomIllegalArgumentsException(ResponseCode.SELLER_NOT_FOUND));
            GetQuoteResponse quote = estimateDeliveryService.getEstimateDeliveryAmount(address_id, seller.getAddress_id(), customerName);
            if (quote != null) {
                cart.setDelivery_charges(fixedDeliveryFee + fixedHandlingFee + fixedSmallCartFee);
                cart_Service.update(cart.getId(), cart, cart.getModified_by());
            } else {
                addressService.findById(address_id).ifPresent(address ->
                        demandingPincodeService.storeDemandingPincode(address.getPincode(), cart.getUser_id()));
            }
        }
        boolean freeDelivery = minCartValueInPaise <= summed;
        cartBean.setItem_total(CommonUtils.paiseToRupee(summed));
        cartBean.setItem_total_mrp(CommonUtils.paiseToRupee(summedMRP));
        cartBean.setTotal_count(total_items);
        cartBean.setCart_items(cartItems);
        cartBean.setDelivery_charge(total_items > 0 ? CommonUtils.paiseToRupee(fixedDeliveryFee + fixedHandlingFee + fixedSmallCartFee) : BigDecimal.ZERO);
        cartBean.setTotal_amount(summed > 0 ? freeDelivery ? CommonUtils.paiseToRupee(summed) : CommonUtils.paiseToRupee(summed + fixedDeliveryFee + fixedHandlingFee + fixedSmallCartFee) : BigDecimal.ZERO);
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
