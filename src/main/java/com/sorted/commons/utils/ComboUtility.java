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
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class ComboUtility {

    private final ComboService comboService;
    private final ProductService productService;

    public List<Products> getProductsByComboId(String comboId) {
        Combo combo = comboService.findById(comboId).orElseThrow(() -> new CustomIllegalArgumentsException(ResponseCode.MISSING_COMBO));

        List<String> productIds = combo.getItem_ids();

        SEFilter filter = new SEFilter(SEFilterType.AND);
        filter.addClause(WhereClause.in(BaseMongoEntity.Fields.id, productIds));
        List<Products> products = productService.repoFind(filter);

        if (CollectionUtils.isEmpty(products)) {
            throw new CustomIllegalArgumentsException(ResponseCode.NO_RECORD);
        }

        boolean notAvailable = products.stream().anyMatch(p -> p.isDeleted() || p.getQuantity().compareTo(0L) <= 0);
        if (notAvailable) {
            throw new CustomIllegalArgumentsException(ResponseCode.COMBO_ITEM_NOT_AVAILABLE);
        }

        return products;
    }

    public List<Products> getProductsByCombo(Combo combo) {

        List<String> productIds = combo.getItem_ids();

        SEFilter filter = new SEFilter(SEFilterType.AND);
        filter.addClause(WhereClause.in(BaseMongoEntity.Fields.id, productIds));
        List<Products> products = productService.repoFind(filter);

        if (CollectionUtils.isEmpty(products)) {
            throw new CustomIllegalArgumentsException(ResponseCode.NO_RECORD);
        }

        boolean notAvailable = products.stream().anyMatch(p -> p.isDeleted() || p.getQuantity().compareTo(0L) <= 0);
        if (notAvailable) {
            throw new CustomIllegalArgumentsException(ResponseCode.COMBO_ITEM_NOT_AVAILABLE);
        }

        return products;
    }

    public boolean isCombo(String productId) {
        return comboService.findById(productId).isPresent();
    }

    public Combo validateAndGetCombo(String comboId) {
        Combo combo = comboService.findById(comboId).orElseThrow(() -> new CustomIllegalArgumentsException(ResponseCode.MISSING_COMBO));
        List<String> productIds = combo.getItem_ids();

        SEFilter filter = new SEFilter(SEFilterType.AND);
        filter.addClause(WhereClause.in(BaseMongoEntity.Fields.id, productIds));
        List<Products> products = productService.repoFind(filter);

        if (CollectionUtils.isEmpty(products)) {
            throw new CustomIllegalArgumentsException(ResponseCode.NO_RECORD);
        }

        boolean notAvailable = products.stream().anyMatch(p -> p.isDeleted() || p.getQuantity().compareTo(0L) <= 0);
        if (notAvailable) {
            throw new CustomIllegalArgumentsException(ResponseCode.COMBO_ITEM_NOT_AVAILABLE);
        }
        return combo;
    }

    public boolean validateCombo(Combo combo) {
        List<String> productIds = combo.getItem_ids();

        SEFilter filter = new SEFilter(SEFilterType.AND);
        filter.addClause(WhereClause.in(BaseMongoEntity.Fields.id, productIds));
        List<Products> products = productService.repoFind(filter);

        if (CollectionUtils.isEmpty(products)) {
            return false;
        }

        boolean notAvailable = products.stream().anyMatch(p -> p.isDeleted() || p.getQuantity().compareTo(0L) <= 0);
        return !notAvailable;
    }

    public List<Combo> getActiveCombos(List<String> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            return Collections.emptyList();
        }
        SEFilter filter = new SEFilter(SEFilterType.AND);
        filter.addClause(WhereClause.in(BaseMongoEntity.Fields.id, productIds));
        filter.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

        List<Combo> combos = comboService.repoFind(filter);
        if (CollectionUtils.isEmpty(combos)) {
            return Collections.emptyList();
        }
        return combos.stream().filter(this::validateCombo).toList();
    }
}
