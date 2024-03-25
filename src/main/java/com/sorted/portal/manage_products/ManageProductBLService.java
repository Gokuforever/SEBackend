package com.sorted.portal.manage_products;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.sorted.portal.entity.mongo.Category_Master;
import com.sorted.portal.entity.mongo.Category_Master.SubCategory;
import com.sorted.portal.entity.mongo.Products;
import com.sorted.portal.entity.mongo.Products.SelectedSubCatagories;
import com.sorted.portal.entity.service.Category_MasterService;
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
@RequestMapping("/product")
public class ManageProductBLService {

	@Autowired
	private ProductService productService;

	@Autowired
	private Category_MasterService category_MasterService;

	@PostMapping("/find")
	public SEResponse find(@RequestBody SERequest request) {
		ProductMISReqBean req = request.getGenericRequestDataObject(ProductMISReqBean.class);

		SEFilter filter = new SEFilter(SEFilterType.AND);
		if (StringUtils.hasText(req.getName())) {
			filter.addClause(WhereClause.like(Products.Fields.name, req.getName()));
		}
		if (StringUtils.hasText(req.getBase_category_code()) || !CollectionUtils.isEmpty(req.getSub_categories())) {
			if (!CollectionUtils.isEmpty(req.getSub_categories())) {
				for (SubCategory sub_cat : req.getSub_categories()) {
					Map<String, String> keymap = new HashMap<>();
					keymap.put(SelectedSubCatagories.Fields.sub_category, sub_cat.getName());
					Map<String, List<?>> valmap = new HashMap<>();
					valmap.put(SelectedSubCatagories.Fields.selected_attributes, sub_cat.getAttributes());
					filter.addClause(WhereClause.elem_match(Products.Fields.selected_sub_catagories, keymap, valmap));
				}
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

		Category_Master selected_category = req.getSelected_category();
		if (selected_category == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.CATALORY_NOT_SELECTED);
		}
		if (!StringUtils.hasText(selected_category.getCategory_code())) {
			throw new CustomIllegalArgumentsException(ResponseCode.CATALORY_NOT_SELECTED);
		}
		SEFilter cfilter = new SEFilter(SEFilterType.AND);
		cfilter.addClause(WhereClause.eq(Category_Master.Fields.category_code, selected_category.getCategory_code()));
		Category_Master category = category_MasterService.repoFindOne(cfilter);
		if (category == null) {
			throw new CustomIllegalArgumentsException(ResponseCode.CATALORY_NOT_FOUND);
		}
		if (CollectionUtils.isEmpty(selected_category.getSub_categories())) {
			throw new CustomIllegalArgumentsException(ResponseCode.EMPTY_SUB_CATEGORY);
		}
		List<SubCategory> db_sub_catagories = category.getSub_categories();
		Map<String, List<String>> sub_category_map = db_sub_catagories.stream()
				.collect(Collectors.toMap(SubCategory::getName, SubCategory::getAttributes));
		List<SubCategory> req_sub_catagories = selected_category.getSub_categories();
		List<SubCategory> sub_cat = new ArrayList<>();
		List<SelectedSubCatagories> selected_sub_cat = new ArrayList<>();
		for (SubCategory SubCategory : req_sub_catagories) {
			if (!StringUtils.hasText(SubCategory.getName())) {
				throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_SUB_CATEGORY);
			}
			if (!sub_category_map.containsKey(SubCategory.getName())) {
				throw new CustomIllegalArgumentsException(ResponseCode.INVALID_SUB_CATEGORY);
			}
			if (CollectionUtils.isEmpty(SubCategory.getAttributes())) {
				throw new CustomIllegalArgumentsException(ResponseCode.EMPTY_ATTRIBUTE);
			}
			List<String> attr = sub_category_map.get(SubCategory.getName());
			if (!attr.containsAll(SubCategory.getAttributes())) {
				throw new CustomIllegalArgumentsException(ResponseCode.INVALID_ATTRIBUTE);
			}
			SelectedSubCatagories subCatagories = new SelectedSubCatagories();
			subCatagories.setSub_category(SubCategory.getName());
			subCatagories.setSelected_attributes(SubCategory.getAttributes());

			selected_sub_cat.add(subCatagories);
			sub_cat.add(SubCategory);
		}
		Products product = new Products();
		product.setName(req.getName());
		product.setSelling_price(new BigDecimal(req.getPrice()));
		product.setQuantity(Long.valueOf(req.getQuantity()));
		product.setDescription(req.getDescription());
		product.setSelected_sub_catagories(selected_sub_cat);
		productService.create(product, Defaults.SYSTEM_ADMIN);
//		for (SubCategory SubCategory : sub_cat) {
//			Product_Category_Mapping category_Mapping = new Product_Category_Mapping();
//			category_Mapping.setProduct_id(create.getId());
//			category_Mapping.setCategory_code(category.getCategory_code());
//			category_Mapping.setCategory_name(category.getName());
//			category_Mapping.setSub_category(SubCategory);
//			product_Category_MappingService.create(category_Mapping, Defaults.SYSTEM_ADMIN);
//		}

		return SEResponse.getEmptySuccessResponse(ResponseCode.SUCCESSFUL);
	}

}
