package com.sorted.portal.manage.catagory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sorted.portal.beans.CatagoryReqBean;
import com.sorted.portal.constants.Defaults;
import com.sorted.portal.entity.mongo.Catagory_Master;
import com.sorted.portal.entity.mongo.Catagory_Master.SubCatagory;
import com.sorted.portal.entity.service.Catagory_MasterService;
import com.sorted.portal.enums.ResponseCode;
import com.sorted.portal.exceptions.CustomIllegalArgumentsException;
import com.sorted.portal.helper.SERequest;
import com.sorted.portal.helper.SEResponse;
import com.sorted.portal.utils.SERegExpUtils;

@RestController
@RequestMapping("/catagory")
public class ManageCatagoryBLService {

	@Autowired
	private Catagory_MasterService catagoryService;

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
//		Catagory_Master selected_catagory = req.getSelected_catagory();
//		CatagoryDetails details = new CatagoryDetails();
//		details.setCatagory_name(selected_catagory.getName());
//		details.setSelected_sub_catagories(selected_catagory.getSub_catagories());
//		product.setCatagories(details);
//		
//		productService.create(product, Defaults.SYSTEM_ADMIN);
//		
//		return SEResponse.getEmptySuccessResponse(ResponseCode.SUCCESSFUL);
//	}

	@PostMapping("/create")
	public SEResponse create(@RequestBody SERequest request) {

		CatagoryReqBean req = request.getGenericRequestDataObject(CatagoryReqBean.class);

		if (!StringUtils.hasText(req.getCatagory_name())) {
			throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_CATAGORY);
		}
		if (Boolean.FALSE.equals(SERegExpUtils.standardTextValidation(req.getCatagory_name()))) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_CATAGORY);
		}
		if (!CollectionUtils.isEmpty(req.getSub_catagories())) {
			for (SubCatagory subCatagory : req.getSub_catagories()) {
				if (!StringUtils.hasText(subCatagory.getName())) {
					throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_SUB_CATAGORY);
				}
				if (Boolean.FALSE.equals(SERegExpUtils.isString(subCatagory.getName()))) {
					throw new CustomIllegalArgumentsException(ResponseCode.INVALID_SUB_CATAGORY);
				}
				if (CollectionUtils.isEmpty(subCatagory.getAttributes())) {
					throw new CustomIllegalArgumentsException(ResponseCode.INVALID_MIN_SUB_CATAGORY);
				}
				long count = subCatagory.getAttributes().stream().filter(e -> !SERegExpUtils.standardTextValidation(e))
						.count();
				if (count > 0) {
					throw new CustomIllegalArgumentsException(ResponseCode.INVALID_SUB_CATAGORY_VAL);
				}
			}
		}

		Catagory_Master catagory = new Catagory_Master();
		catagory.setName(req.getCatagory_name());
		catagory.setSub_catagories(req.getSub_catagories());

		catagoryService.create(catagory, Defaults.SYSTEM_ADMIN);

		return SEResponse.getEmptySuccessResponse(ResponseCode.SUCCESSFUL);
	}
}
