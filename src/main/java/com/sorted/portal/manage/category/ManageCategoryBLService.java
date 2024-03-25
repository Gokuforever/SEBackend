package com.sorted.portal.manage.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sorted.portal.beans.CategoryReqBean;
import com.sorted.portal.constants.Defaults;
import com.sorted.portal.entity.mongo.Category_Master;
import com.sorted.portal.entity.mongo.Category_Master.SubCategory;
import com.sorted.portal.entity.service.Category_MasterService;
import com.sorted.portal.enums.ResponseCode;
import com.sorted.portal.exceptions.CustomIllegalArgumentsException;
import com.sorted.portal.helper.SERequest;
import com.sorted.portal.helper.SEResponse;
import com.sorted.portal.utils.SERegExpUtils;

@RestController
@RequestMapping("/category")
public class ManageCategoryBLService {

	@Autowired
	private Category_MasterService categoryService;

//	@Autowired
//	private ProductService productService;

//	@PostMapping("/create/product")
//	public SEResponse create_product(@RequestBody SERequest request) {
//		ProductMISReqBean req = request.getGenericRequestDataObject(ProductMISReqBean.class);
//		
//		Products product = new Products();
//		product.setName(req.getName());
//		product.setStock(req.getStock());
//		product.setPrice(req.getPrice());
//		Category_Master selected_category = req.getSelected_category();
//		CategoryDetails details = new CategoryDetails();
//		details.setCategory_name(selected_category.getName());
//		details.setSelected_sub_catagories(selected_category.getSub_categories());
//		product.setCatagories(details);
//		
//		productService.create(product, Defaults.SYSTEM_ADMIN);
//		
//		return SEResponse.getEmptySuccessResponse(ResponseCode.SUCCESSFUL);
//	}

	@PostMapping("/create")
	public SEResponse create(@RequestBody SERequest request) {

		CategoryReqBean req = request.getGenericRequestDataObject(CategoryReqBean.class);

		if (!StringUtils.hasText(req.getCategory_name())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_CATEGORY);
		}
		if (Boolean.FALSE.equals(SERegExpUtils.standardTextValidation(req.getCategory_name()))) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_CATEGORY);
		}
		if (!CollectionUtils.isEmpty(req.getSub_categories())) {
			for (SubCategory subCategory : req.getSub_categories()) {
				if (!StringUtils.hasText(subCategory.getName())) {
					throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_SUB_CATEGORY);
				}
				if (Boolean.FALSE.equals(SERegExpUtils.isString(subCategory.getName()))) {
					throw new CustomIllegalArgumentsException(ResponseCode.INVALID_SUB_CATEGORY);
				}
				if (CollectionUtils.isEmpty(subCategory.getAttributes())) {
					throw new CustomIllegalArgumentsException(ResponseCode.INVALID_MIN_SUB_CATEGORY);
				}
				long count = subCategory.getAttributes().stream().filter(e -> !SERegExpUtils.standardTextValidation(e))
						.count();
				if (count > 0) {
					throw new CustomIllegalArgumentsException(ResponseCode.INVALID_SUB_CATEGORY_VAL);
				}
			}
		}

		Category_Master category = new Category_Master();
		category.setName(req.getCategory_name());
		category.setSub_categories(req.getSub_categories());

		categoryService.create(category, Defaults.SYSTEM_ADMIN);

		return SEResponse.getEmptySuccessResponse(ResponseCode.SUCCESSFUL);
	}
}
