package com.sorted.portal.manage_cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sorted.portal.beans.CartCRUDReqBean;
import com.sorted.portal.beans.Item;
import com.sorted.portal.entity.mongo.BaseMongoEntity;
import com.sorted.portal.entity.mongo.Cart;
import com.sorted.portal.entity.mongo.Products;
import com.sorted.portal.entity.service.Cart_Service;
import com.sorted.portal.entity.service.ProductService;
import com.sorted.portal.enums.ResponseCode;
import com.sorted.portal.exceptions.CustomIllegalArgumentsException;
import com.sorted.portal.helper.AggregationFilter.SEFilter;
import com.sorted.portal.helper.AggregationFilter.SEFilterType;
import com.sorted.portal.helper.AggregationFilter.WhereClause;
import com.sorted.portal.helper.SERequest;
import com.sorted.portal.helper.SEResponse;
import com.sorted.portal.utils.SERegExpUtils;

@RestController
@RequestMapping("/cart")
public class ManageCartBL_Service {

	@Autowired
	private Cart_Service cart_Service;

	@Autowired
	private ProductService productService;

	@PostMapping("/add")
	public SEResponse add(@RequestBody SERequest request) {

		CartCRUDReqBean req = request.getGenericRequestDataObject(CartCRUDReqBean.class);
		if (!StringUtils.hasText(req.getReq_user_id())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_USER_ID);
		}
		if (!StringUtils.hasText(req.getProduct_id())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_PRODUCT_ID);
		}
		if (!StringUtils.hasText(req.getQuantity())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_PRODUCT_QUANTITY);
		}
		if (Boolean.FALSE.equals(SERegExpUtils.isQuantity(req.getQuantity()))) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVAALID_PRODUCT_QUANTITY);
		}

		SEFilter filterP = new SEFilter(SEFilterType.AND);
		filterP.addClause(WhereClause.eq(Products.Fields.product_code, req.getProduct_id()));
		filterP.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		Products product = productService.repoFindOne(filterP);
		if (product == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.PRODUCT_NOT_FOUND);
		}
		Long quantity = product.getQuantity();
		if (quantity == null || quantity < 1) {
			throw new CustomIllegalArgumentsException(ResponseCode.PRODUCT_OUT_OF_STOCK);
		}

		SEFilter filterC = new SEFilter(SEFilterType.AND);
		filterC.addClause(WhereClause.eq(Cart.Fields.user_id, req.getReq_user_id()));
		filterC.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
		Cart cart = cart_Service.repoFindOne(filterC);
		if (cart == null) {
			cart = new Cart();
			cart.setUser_id(req.getReq_user_id());
			cart = cart_Service.create(cart, req.getReq_user_id());
		}

		long new_quantity = Long.parseLong(req.getQuantity());
		BigDecimal product_price = product.getSelling_price();

		List<Item> cart_items = cart.getCart_items();
		if (!CollectionUtils.isEmpty(cart_items)) {
			Optional<Item> optional = cart_items.stream().filter(e -> e.getProduct_id().equals(product.getId()))
					.findFirst();
			if (optional.isPresent()) {
				cart_items.remove(optional.get());
			}
		} else {
			cart_items = new ArrayList<>();
		}
		if (new_quantity > quantity) {
			throw new CustomIllegalArgumentsException(ResponseCode.STOCK_LIMIT_REACHED);
		}
		BigDecimal sub_total = product_price.multiply(BigDecimal.valueOf(new_quantity));
		Item item = new Item();
		item.setProduct_code(product.getProduct_code());
		item.setProduct_id(product.getId());
		item.setQuantity(new_quantity);
		item.setPrice(product_price);
		item.setSub_total(sub_total);
		cart_items.add(item);
		BigDecimal total_amount = cart_items.stream().map(Item::getSub_total).reduce(BigDecimal.ZERO, BigDecimal::add);
		cart.setTotal_price(total_amount);
		cart.setCart_items(cart_items);

		cart_Service.update(cart.getId(), cart, req.getReq_user_id());
		return SEResponse.getEmptySuccessResponse(ResponseCode.SUCCESSFUL);
	}

	@PostMapping("/update")
	public SEResponse update(@RequestBody SERequest request) {
		CartCRUDReqBean req = request.getGenericRequestDataObject(CartCRUDReqBean.class);
		if (!StringUtils.hasText(req.getReq_user_id())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_USER_ID);
		}
		if (!StringUtils.hasText(req.getProduct_id())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_PRODUCT_ID);
		}
		if (!StringUtils.hasText(req.getQuantity())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_PRODUCT_QUANTITY);
		}
		if (Boolean.FALSE.equals(SERegExpUtils.isQuantity(req.getQuantity()))) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVAALID_PRODUCT_QUANTITY);
		}

		SEFilter filterP = new SEFilter(SEFilterType.AND);
		filterP.addClause(WhereClause.eq(Products.Fields.product_code, req.getProduct_id()));
		filterP.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		Products product = productService.repoFindOne(filterP);
		if (product == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.PRODUCT_NOT_FOUND);
		}
		Long quantity = product.getQuantity();
		if (quantity == null || quantity < 1) {
			throw new CustomIllegalArgumentsException(ResponseCode.PRODUCT_OUT_OF_STOCK);
		}
		SEFilter filterC = new SEFilter(SEFilterType.AND);
		filterC.addClause(WhereClause.eq(Cart.Fields.user_id, req.getReq_user_id()));
		filterC.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
		Cart cart = cart_Service.repoFindOne(filterC);
		if (cart == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.CART_NOT_FOUND);
		}
		Long new_quantity = Long.valueOf(req.getQuantity());

		List<Item> cart_items = cart.getCart_items();

		if (!CollectionUtils.isEmpty(cart_items)) {
			if (new_quantity == 0) {
				List<Item> updated_cart = cart_items.stream().filter(e -> !e.getProduct_id().equals(product.getId()))
						.collect(Collectors.toList());
				cart_items.clear();
				if (!CollectionUtils.isEmpty(updated_cart)) {
					cart_items.addAll(updated_cart);
				}
			} else {
				Optional<Item> optional = cart_items.stream().filter(e -> e.getProduct_id().equals(product.getId()))
						.findFirst();

				if (optional.isPresent()) {
					cart_items.remove(optional.get());
				}
			}
		} else {
			cart_items = new ArrayList<>();
		}
		if (new_quantity > 0) {
			if (new_quantity > quantity) {
				throw new CustomIllegalArgumentsException(ResponseCode.STOCK_LIMIT_REACHED);
			}
			BigDecimal product_price = product.getSelling_price();
			BigDecimal sub_total = product_price.multiply(BigDecimal.valueOf(new_quantity));
			Item item = new Item();
			item.setProduct_code(product.getProduct_code());
			item.setProduct_id(product.getId());
			item.setQuantity(new_quantity);
			item.setPrice(product_price);
			item.setSub_total(sub_total);
			cart_items.add(item);
		}

		cart.setCart_items(cart_items);

		cart_Service.update(cart.getId(), cart, req.getReq_user_id());

		return SEResponse.getEmptySuccessResponse(ResponseCode.SUCCESSFUL);
	}

	@PostMapping("/get")
	public SEResponse get(@RequestBody SERequest request) {
		CartCRUDReqBean req = request.getGenericRequestDataObject(CartCRUDReqBean.class);
		if (!StringUtils.hasText(req.getReq_user_id())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_USER_ID);
		}
		SEFilter filterC = new SEFilter(SEFilterType.AND);
		filterC.addClause(WhereClause.eq(Cart.Fields.user_id, req.getReq_user_id()));
		filterC.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
		Cart cart = cart_Service.repoFindOne(filterC);
		if (cart == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.NO_RECORD);
		}
		return SEResponse.getBasicSuccessResponseObject(cart, ResponseCode.SUCCESSFUL);
	}
}
