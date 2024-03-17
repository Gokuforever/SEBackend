package com.sorted.portal.manage_products;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sorted.portal.beans.ProductMISReqBean;
import com.sorted.portal.beans.ProductReqBean;
import com.sorted.portal.constants.Defaults;
import com.sorted.portal.entity.mongo.BaseMongoEntity;
import com.sorted.portal.entity.mongo.Catagory_Master;
import com.sorted.portal.entity.mongo.Catagory_Master.SubCatagory;
import com.sorted.portal.entity.mongo.Product_Catagory_Mapping;
import com.sorted.portal.entity.mongo.Products;
import com.sorted.portal.entity.service.Catagory_MasterService;
import com.sorted.portal.entity.service.ProductService;
import com.sorted.portal.entity.service.Product_Catagory_MappingService;
import com.sorted.portal.enums.ResponseCode;
import com.sorted.portal.exceptions.CustomIllegalArgumentsException;
import com.sorted.portal.helper.AggregationFilter.SEFilter;
import com.sorted.portal.helper.AggregationFilter.SEFilterNode;
import com.sorted.portal.helper.AggregationFilter.SEFilterType;
import com.sorted.portal.helper.AggregationFilter.WhereClause;
import com.sorted.portal.helper.SERequest;
import com.sorted.portal.helper.SEResponse;
import com.sorted.portal.utils.CommonUtils;
import com.sorted.portal.utils.SERegExpUtils;

@RestController
@RequestMapping("/product")
public class ManageProductBLService {

	@Autowired
	private ProductService productService;

	@Autowired
	private Catagory_MasterService catagory_MasterService;

	@Autowired
	private Product_Catagory_MappingService product_Catagory_MappingService;

	@PostMapping("/find")
	public SEResponse find(@RequestBody SERequest request) {
		ProductMISReqBean req = request.getGenericRequestDataObject(ProductMISReqBean.class);

		SEFilter filter = new SEFilter(SEFilterType.AND);
		if (StringUtils.hasText(req.getName())) {
			filter.addClause(WhereClause.like(Products.Fields.name, req.getName()));
		}
		if (StringUtils.hasText(req.getBase_ctagory_code()) || !CollectionUtils.isEmpty(req.getSub_catagories())) {
			SEFilter pcmFilter = new SEFilter(SEFilterType.AND);
			if (StringUtils.hasText(req.getBase_ctagory_code())) {
				pcmFilter.addClause(
						WhereClause.eq(Product_Catagory_Mapping.Fields.catagory_code, req.getBase_ctagory_code()));
			}
			if (!CollectionUtils.isEmpty(req.getSub_catagories())) {
				pcmFilter.setSubquery_type(SEFilterType.OR);
				List<SEFilterNode> nodes = new ArrayList<>();
				for (SubCatagory sub_cat : req.getSub_catagories()) {
					SEFilterNode filterNode = new SEFilterNode(SEFilterType.AND);
					filterNode.addClause(WhereClause.eq("sub_catagory.name", sub_cat.getName()));
					filterNode.addClause(WhereClause.in("sub_catagory.attributes", sub_cat.getAttributes()));
//					filterNode.addClause(WhereClause.all("sub_catagory.attributes", sub_cat.getAttributes()));
					nodes.add(filterNode);
				}
				pcmFilter.setNodes(nodes);
			}
			pcmFilter.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

			List<Product_Catagory_Mapping> listPCM = product_Catagory_MappingService.repoFind(pcmFilter);
			if (CollectionUtils.isEmpty(listPCM)) {
				return SEResponse.getEmptySuccessResponse(ResponseCode.NO_RECORD);
			}
			Set<String> product_ids = listPCM.stream().map(Product_Catagory_Mapping::getProduct_id)
					.collect(Collectors.toSet());
			product_ids.remove(null);
			if (CollectionUtils.isEmpty(product_ids)) {
				filter.addClause(WhereClause.in(BaseMongoEntity.Fields.deleted, CommonUtils.convertS2L(product_ids)));
			}

		}

		filter.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
		List<Products> listProducts = productService.repoFind(filter);
		if (CollectionUtils.isEmpty(listProducts)) {
			return SEResponse.getEmptySuccessResponse(ResponseCode.NO_RECORD);
		}

		return SEResponse.getBasicSuccessResponseList(listProducts, ResponseCode.SUCCESSFUL);

	}

	@PostMapping("/create")
	public SEResponse create(@RequestBody SERequest request) {
		ProductReqBean req = request.getGenericRequestDataObject(ProductReqBean.class);

		if (!StringUtils.hasText(req.getName())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_PRODUCT_NAME);
		}
		if (!SERegExpUtils.standardTextValidation(req.getName())) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_PRODUCT_NAME);
		}
		if (!StringUtils.hasText(req.getPrice())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_PRODUCT_PRICE);
		}
		if (!SERegExpUtils.isPrice(req.getPrice())) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_PRODUCT_PRICE);
		}
		if (!StringUtils.hasText(req.getQuantity())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_QUANTITY);
		}
		if (!SERegExpUtils.isQuantity(req.getQuantity())) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_QUANTITY);
		}
		if (!StringUtils.hasText(req.getDescription())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MISSING_PRODUCT_DESCRIPTION);
		}
		if (!SERegExpUtils.standardTextValidation(req.getDescription())) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_PRODUCT_DESCRIPTION);
		}

		Catagory_Master selected_catagory = req.getSelected_catagory();
		if (selected_catagory == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.CATALORY_NOT_SELECTED);
		}
		if (!StringUtils.hasText(selected_catagory.getCatagory_code())) {
			throw new CustomIllegalArgumentsException(ResponseCode.CATALORY_NOT_SELECTED);
		}
		SEFilter cfilter = new SEFilter(SEFilterType.AND);
		cfilter.addClause(WhereClause.eq(Catagory_Master.Fields.catagory_code, selected_catagory.getCatagory_code()));
		Catagory_Master catagory = catagory_MasterService.repoFindOne(cfilter);
		if (catagory == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.CATALORY_NOT_FOUND);
		}
		if (CollectionUtils.isEmpty(selected_catagory.getSub_catagories())) {
			throw new CustomIllegalArgumentsException(ResponseCode.EMPTY_SUB_CATAGORY);
		}
		List<SubCatagory> db_sub_catagories = catagory.getSub_catagories();
		Map<String, List<String>> sub_catagory_map = db_sub_catagories.stream()
				.collect(Collectors.toMap(SubCatagory::getName, SubCatagory::getAttributes));
		List<SubCatagory> req_sub_catagories = selected_catagory.getSub_catagories();
		List<SubCatagory> sub_cat = new ArrayList<>();
		for (SubCatagory subCatagory : req_sub_catagories) {
			if (!StringUtils.hasText(subCatagory.getName())) {
				throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_SUB_CATAGORY);
			}
			if (!sub_catagory_map.containsKey(subCatagory.getName())) {
				throw new CustomIllegalArgumentsException(ResponseCode.INVALID_SUB_CATAGORY);
			}
			if (CollectionUtils.isEmpty(subCatagory.getAttributes())) {
				throw new CustomIllegalArgumentsException(ResponseCode.EMPTY_ATTRIBUTE);
			}
			List<String> attr = sub_catagory_map.get(subCatagory.getName());
			if (!attr.containsAll(subCatagory.getAttributes())) {
				throw new CustomIllegalArgumentsException(ResponseCode.INVALID_ATTRIBUTE);
			}
			sub_cat.add(subCatagory);
		}
		Products product = new Products();
		product.setName(req.getName());
		product.setPrice(new BigDecimal(req.getPrice()));
		product.setQuantity(Long.valueOf(req.getQuantity()));
		product.setDescription(req.getDescription());

		Products create = productService.create(product, Defaults.SYSTEM_ADMIN);
		for (SubCatagory subCatagory : sub_cat) {
			Product_Catagory_Mapping catagory_Mapping = new Product_Catagory_Mapping();
			catagory_Mapping.setProduct_id(create.getId());
			catagory_Mapping.setCatagory_code(catagory.getCatagory_code());
			catagory_Mapping.setCatagory_name(catagory.getName());
			catagory_Mapping.setSub_catagory(subCatagory);
			product_Catagory_MappingService.create(catagory_Mapping, Defaults.SYSTEM_ADMIN);
		}

		return SEResponse.getEmptySuccessResponse(ResponseCode.SUCCESSFUL);
	}

}
