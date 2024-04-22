package com.sorted.portal.manage.payments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cashfree.ApiException;
import com.cashfree.ApiResponse;
import com.cashfree.Cashfree;
import com.cashfree.model.CreateOrderRequest;
import com.cashfree.model.CustomerDetails;
import com.cashfree.model.OrderEntity;
import com.cashfree.model.OrderMeta;
import com.google.gson.Gson;
import com.sorted.portal.beans.Item;
import com.sorted.portal.beans.PG_Response_Bean;
import com.sorted.portal.entity.mongo.BaseMongoEntity;
import com.sorted.portal.entity.mongo.Cart;
import com.sorted.portal.entity.mongo.Order_Details;
import com.sorted.portal.entity.mongo.Products;
import com.sorted.portal.entity.mongo.Third_Party_Api;
import com.sorted.portal.entity.mongo.Users;
import com.sorted.portal.entity.service.Cart_Service;
import com.sorted.portal.entity.service.Order_Details_Service;
import com.sorted.portal.entity.service.ProductService;
import com.sorted.portal.entity.service.Third_Party_Api_Service;
import com.sorted.portal.entity.service.Users_Service;
import com.sorted.portal.enums.ResponseCode;
import com.sorted.portal.exceptions.CustomIllegalArgumentsException;
import com.sorted.portal.helper.AggregationFilter.SEFilter;
import com.sorted.portal.helper.AggregationFilter.SEFilterType;
import com.sorted.portal.helper.AggregationFilter.WhereClause;
import com.sorted.portal.helper.SERequest;
import com.sorted.portal.helper.SEResponse;
import com.sorted.portal.payment.beans.CreateOrderReq;

@RestController
public class ManagePayment_BLService {

	@Autowired
	private Users_Service users_Service;

	@Autowired
	private Cart_Service cart_Service;

	@Autowired
	private Order_Details_Service order_Details_Service;

	@Autowired
	private Third_Party_Api_Service third_Party_Api_Service;

	@Autowired
	private ProductService productService;

	@Value("${se.base_url}")
	private String base_url;

	@Value("${cashfree.api.version}")
	private String cashfree_api_version;

	@Value("${cashfree.client.id}")
	private String cashfree_client_id;

	@Value("${cashfree.client.secret}")
	private String cashfree_client_secret;

	@PostMapping("/create/order")
	public OrderEntity createOrder() {
//		req.getGenericRequestDataObject(CreateOrderReq.class);

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomerId("123");
		customerDetails.setCustomerPhone("9999999999");

		CreateOrderRequest request = new CreateOrderRequest();
		request.setOrderAmount(1.0);
		request.setOrderCurrency("INR");
		request.setCustomerDetails(customerDetails);

		OrderMeta meta = new OrderMeta();
		meta.setReturnUrl("http://localhost:3000/");
//		meta.setPaymentMethods("upi,cc");

		request.orderMeta(meta);
		try {
			Cashfree cashfree = new Cashfree();
			Cashfree.XClientId = cashfree_client_id;
			Cashfree.XClientSecret = cashfree_client_secret;
			Cashfree.XEnvironment = Cashfree.SANDBOX;
			ApiResponse<OrderEntity> response = cashfree.PGCreateOrder(cashfree_api_version, request, null, null, null);
			System.out.println(response.getData());
			return response.getData();

		} catch (ApiException e) {
			throw new RuntimeException(e);
		}
	}

	@PostMapping("/create/order/v2")
	public SEResponse createOrder(@RequestBody SERequest req) {
		CreateOrderReq bean = req.getGenericRequestDataObject(CreateOrderReq.class);

		if (!StringUtils.hasText(bean.getUser_id())) {
			throw new CustomIllegalArgumentsException("Customer not found.");
		}

		SEFilter filterU = new SEFilter(SEFilterType.AND);
		filterU.addClause(WhereClause.eq(BaseMongoEntity.Fields.id, bean.getUser_id()));
		filterU.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		Users customer = users_Service.repoFindOne(filterU);
		if (customer == null) {
			throw new CustomIllegalArgumentsException("Customer not found.");
		}

		SEFilter filterC = new SEFilter(SEFilterType.AND);
		filterC.addClause(WhereClause.eq(Cart.Fields.user_id, customer.getId()));
		filterC.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
		Cart cart = cart_Service.repoFindOne(filterC);
		if (cart == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.NO_RECORD);
		}

		// implement kafka for to update product quantity
		List<Item> cart_items = cart.getCart_items();
		if (CollectionUtils.isEmpty(cart_items)) {
			throw new CustomIllegalArgumentsException("Cart is Empty");
		}
		List<String> product_ids = cart_items.stream().map(Item::getProduct_id).collect(Collectors.toSet()).stream()
				.collect(Collectors.toList());

		SEFilter filterP = new SEFilter(SEFilterType.AND);
		filterP.addClause(WhereClause.in(BaseMongoEntity.Fields.id, product_ids));
		filterP.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		List<Products> listP = productService.repoFind(filterP);
		if (CollectionUtils.isEmpty(listP)) {
			throw new CustomIllegalArgumentsException("Product details not found.");
		}
		Map<String, Products> mapP = listP.stream().collect(Collectors.toMap(Products::getId, e -> e));
		for (Item item : cart_items) {
			if (!mapP.containsKey(item.getProduct_id())) {
				throw new CustomIllegalArgumentsException("Product details not found.");
			}
			Products product = mapP.get(item.getProduct_id());
			long latest_quantity = product.getQuantity() - item.getQuantity();
			product.setQuantity(latest_quantity);
			// what to put in CudBy? - order id??
			productService.update(product.getId(), product, "ON_PURCHASE");
		}

		cart.setCart_items(new ArrayList<>());
		cart_Service.update(cart.getId(), cart, "ON_PURCHASE");

		Order_Details order_Details = new Order_Details();
		order_Details.setUser_id(customer.getId());
		order_Details.setUser_code(customer.getCode());
		order_Details.setItems(cart.getCart_items());
//		order_Details.setStatus(OrderStatus.PENDING.getId());

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomerId(customer.getId());
		customerDetails.setCustomerPhone(customer.getMobile_no());

		CreateOrderRequest request = new CreateOrderRequest();
		request.setOrderAmount(cart.getTotal_price().doubleValue());
		request.setOrderCurrency("INR");
		request.setCustomerDetails(customerDetails);

		Order_Details details = order_Details_Service.create(order_Details, customer.getId());

		OrderMeta meta = new OrderMeta();
		StringBuilder builder = new StringBuilder();
		builder.append(base_url);
		builder.append(details.getId());
		meta.setReturnUrl(builder.toString());
//		meta.setPaymentMethods("upi,cc");

		request.orderMeta(meta);
		Gson gson = new Gson();
		Third_Party_Api third_Party_Api = new Third_Party_Api();
		third_Party_Api.setRaw_request(gson.toJson(request));

		third_Party_Api_Service.create(third_Party_Api, "");

		try {
			Cashfree cashfree = new Cashfree();
			Cashfree.XClientId = cashfree_client_id;
			Cashfree.XClientSecret = cashfree_client_secret;
			Cashfree.XEnvironment = Cashfree.SANDBOX;
			ApiResponse<OrderEntity> response = cashfree.PGCreateOrder(cashfree_api_version, request, null, null, null);
			if (response == null || response.getData() == null) {
				throw new CustomIllegalArgumentsException("Order creation failed.");
			}
			third_Party_Api.setRaw_response(gson.toJson(response));
			third_Party_Api_Service.update(third_Party_Api.getId(), third_Party_Api, "");
			OrderEntity orderEntity = response.getData();
			details.setPg_order_id(orderEntity.getOrderId());
//			orderEntity.getOrderStatus();
			order_Details_Service.update(details.getId(), details, "Cashfree");
			return SEResponse.getBasicSuccessResponseObject(response.getData(), ResponseCode.SUCCESSFUL);
		} catch (ApiException e) {
			throw new RuntimeException(e);
		}
	}

	@PostMapping
	public SEResponse update_order_status(@RequestBody SERequest request) {
		PG_Response_Bean req = request.getGenericRequestDataObject(PG_Response_Bean.class);
		if (!StringUtils.hasText(req.getOrder_id())) {
			throw new CustomIllegalArgumentsException("PG name is missing");
		}
		SEFilter filterOD = new SEFilter(SEFilterType.AND);
		filterOD.addClause(WhereClause.eq(BaseMongoEntity.Fields.id, req.getOrder_id()));
		filterOD.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

		Order_Details order_Details = order_Details_Service.repoFindOne(filterOD);
		if (order_Details == null) {
			throw new CustomIllegalArgumentsException("Order details not found");
		}

		Cashfree cashfree = new Cashfree();
		Cashfree.XClientId = cashfree_client_id;
		Cashfree.XClientSecret = cashfree_client_secret;
		Cashfree.XEnvironment = Cashfree.SANDBOX;
		try {
			ApiResponse<OrderEntity> pgFetchOrder = cashfree.PGFetchOrder(cashfree_api_version,
					order_Details.getPg_order_id(), "", UUID.randomUUID(), null);

		} catch (ApiException e) {
			e.printStackTrace();
		}

		return null;

	}

}
