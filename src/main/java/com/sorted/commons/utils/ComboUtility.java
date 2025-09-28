package com.sorted.commons.utils;

import com.sorted.commons.entity.mongo.BaseMongoEntity;
import com.sorted.commons.entity.mongo.Combo;
import com.sorted.commons.entity.mongo.Products;
import com.sorted.commons.entity.service.ComboService;
import com.sorted.commons.entity.service.ProductService;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import com.sorted.commons.helper.AggregationFilter.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ComboUtility {

    private final ComboService comboService;
    private final ProductService productService;

    public void getProductsByComboId(String comboId) {
        Combo combo = comboService.findById(comboId).orElseThrow(() -> new CustomIllegalArgumentsException(ResponseCode.MISSING_COMBO));

        List<String> productIds = combo.getItem_ids();

        SEFilter filter = new SEFilter(SEFilterType.AND);
        filter.addClause(WhereClause.in(BaseMongoEntity.Fields.id, productIds));
        filter.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
        List<Products> products = productService.repoFind(filter);

        if(CollectionUtils.isEmpty(products)) {
            throw new CustomIllegalArgumentsException(ResponseCode.NO_RECORD);
        }

        
    }
}
