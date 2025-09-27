package com.sorted.commons.utils;

import com.sorted.commons.beans.CartItems;
import com.sorted.commons.constants.Defaults;
import com.sorted.commons.entity.mongo.BaseMongoEntity;
import com.sorted.commons.entity.mongo.Cart;
import com.sorted.commons.entity.mongo.Order_Item;
import com.sorted.commons.entity.mongo.Products;
import com.sorted.commons.entity.service.Cart_Service;
import com.sorted.commons.entity.service.Order_Item_Service;
import com.sorted.commons.entity.service.ProductService;
import com.sorted.commons.enums.All_Status;
import com.sorted.commons.helper.AggregationFilter.SEFilter;
import com.sorted.commons.helper.AggregationFilter.SEFilterType;
import com.sorted.commons.helper.AggregationFilter.WhereClause;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductService productService;
    private final Order_Item_Service orderItemService;
    private final Cart_Service cartService;

    public void increaseProductQuantity(Products product, Long quantity) {
        quantity = product.getQuantity() + quantity;
        product.setQuantity(quantity);
        productService.update(product.getId(), product, Defaults.SYSTEM_ADMIN);
    }

    @Async
    public void reduceProductQuantity(List<Products> listP, Map<String, Long> mapPQ) {
        for (Products product : listP) {
            Long quantity = mapPQ.getOrDefault(product.getId(), null);
            if (quantity == null) {
                continue;
            }
            quantity = product.getQuantity() - quantity;
            product.setQuantity(quantity);
            productService.update(product.getId(), product, Defaults.SYSTEM_ADMIN);
        }
    }

    @Async
    public void reduceProductQuantity(List<CartItems> cartItems) {

        List<String> productIds = cartItems.stream().filter(e -> e.getCurrent_status().equals(All_Status.ProductCurrentStatus.IN_STOCK.getStatus_id())).map(CartItems::getProduct_id).toList();

        SEFilter filterP = new SEFilter(SEFilterType.AND);
        filterP.addClause(WhereClause.in(BaseMongoEntity.Fields.id, productIds));
        filterP.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

        List<Products> listP = productService.repoFind(filterP);
        if (CollectionUtils.isEmpty(listP)) {
            return;
        }

        Map<String, Products> productsMap = listP.stream().collect(Collectors.toMap(Products::getId, p -> p));

        for (CartItems cartItem : cartItems) {
            Products products = productsMap.getOrDefault(cartItem.getProduct_id(), null);
            if (products == null) {
                continue;
            }
            if (!cartItem.getCurrent_status().equals(All_Status.ProductCurrentStatus.IN_STOCK.getStatus_id())) {
                continue;
            }
            long quantity = products.getQuantity() - cartItem.getQuantity();
            products.setQuantity(quantity);
            productService.update(products.getId(), products, Defaults.SYSTEM_ADMIN);
        }
    }

    @Async
    public void emptyCart(String cartId, String cudBy) {
        Optional<Cart> cartById = cartService.findById(cartId);
        if (cartById.isEmpty()) return;
        Cart cart = cartById.get();
        cart.setCart_items(new ArrayList<>());
        cart.setCouponCode(null);
        cart.setDelivery_charges(0L);
        cart.setSmall_cart_fee(0L);
        cart.setHandling_charges(0L);
        cart.setTotal_price(BigDecimal.ZERO);
        cartService.update(cartId, cart, cudBy);
    }

    @Async
    public void createOrderItems(List<Order_Item> listOI, String orderId, String orderCode, String cudBy) {
        for (Order_Item order_Item : listOI) {
            order_Item.setOrder_id(orderId);
            order_Item.setOrder_code(orderCode);
            orderItemService.create(order_Item, cudBy);
        }
    }
}