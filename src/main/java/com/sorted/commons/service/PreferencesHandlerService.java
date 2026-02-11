package com.sorted.commons.service;

import com.sorted.commons.beans.*;
import com.sorted.commons.constants.Defaults;
import com.sorted.commons.entity.mongo.*;
import com.sorted.commons.entity.service.*;
import com.sorted.commons.enums.AssetType;
import com.sorted.commons.helper.AggregationFilter.SEFilter;
import com.sorted.commons.helper.AggregationFilter.SEFilterType;
import com.sorted.commons.helper.AggregationFilter.WhereClause;
import com.sorted.commons.repository.mongo.ProductRepository;
import com.sorted.commons.utils.ComboUtility;
import com.sorted.commons.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreferencesHandlerService {

    private final ZoneHandlerService zoneHandlerService;
    private final Product_Master_Service productMasterService;
    private final HomeConfigService homeConfigService;
    private final ProductService productService;
    private final AssetsService assetsService;
    private final ComboService comboService;
    private final ComboUtility comboUtility;
    private final ProductRepository productRepository;
    private final CategoryFilterServiceV2 categoryFilterService;
    private final Users_Service usersService;

    public Config fetchPreference(double lat, double lng, Users users) {
        ZoneEntity zoneEntity = zoneHandlerService.identifyZone(lat, lng);

        users.setNearestZoneId(zoneEntity.getZoneId());
        usersService.update(users.getId(), users, Defaults.SYSTEM_ADMIN);

        Seller seller = zoneHandlerService.getSellerByZone(zoneEntity.getZoneId(), lat, lng);
        SEFilter filter = new SEFilter(SEFilterType.AND);
        filter.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
        List<Product_Master> productMasters = productMasterService.repoFind(filter);

        Map<String, Product_Master> mapPM = productMasters.stream().collect(Collectors.toMap(Product_Master::getId, e -> e));
        List<String> productMasterIds = CommonUtils.convertS2L(mapPM.keySet());

        SEFilter filter2 = new SEFilter(SEFilterType.AND);
        filter2.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
        filter2.addClause(WhereClause.in(Products.Fields.product_master_id, productMasterIds));
        List<Products> products = productService.repoFind(filter2);

        Map<String, List<Products>> listMap = products.stream().collect(Collectors.groupingBy(Products::getProduct_master_id));

        Map<String, Long> highestPrize = new HashMap<>();

        for (Map.Entry<String, List<Products>> entry : listMap.entrySet()) {
            long max = 0L;
            for (Products product : entry.getValue()) {
                Long sellingPrice = product.getSelling_price();
                max = Math.max(max, sellingPrice);
            }
            highestPrize.put(entry.getKey(), max);
        }

        Map<String, Products> productsMapBySeller = products.stream().filter(e -> e.getSeller_id().equals(seller.getId())).collect(Collectors.toMap(p -> p.getProduct_master_id(), p -> p));

        List<ProductBean> productBeans = new ArrayList<>();
        for (Product_Master productMaster : productMasters) {
            Products product = productsMapBySeller.getOrDefault(productMaster.getId(), null);
            if (product == null) {
                productBeans.add(getProductBean(productMaster));
            } else {
                long maxSellingPrize = highestPrize.getOrDefault(productMaster.getId(), 0L);
                if (maxSellingPrize > 0L) {
                    product.setSelling_price(maxSellingPrize);
                }
                productBeans.add(getProductBean(product));
            }
        }

        HomeProductsBean.HomeProductsBeanBuilder homeProductsBeanBuilder = HomeProductsBean.builder();

        List<HomeConfig> homeConfigs = homeConfigService.repoFindAll();

        List<HomeProductsBean> homeProductsBeans = new ArrayList<>();


        Set<String> productIdsBySeller = productsMapBySeller.keySet();
        if (!CollectionUtils.isEmpty(productIdsBySeller)) {
            for (HomeConfig homeConfig : homeConfigs) {

                String categoryId = homeConfig.getCategoryId();
                homeProductsBeanBuilder.mainBadge(homeConfig.getMainBadge())
                        .mainTitle(homeConfig.getMainTitle())
                        .mainSubtitle(homeConfig.getMainSubtitle())
                        .categoryId(categoryId);

                SEFilter filter3 = new SEFilter(SEFilterType.AND);
                filter3.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
                filter3.addClause(WhereClause.eq(Products.Fields.category_id, categoryId));
                filter3.addClause(WhereClause.isNotEmpty("media.cdn_url"));
                filter3.addClause(WhereClause.in(BaseMongoEntity.Fields.id, CommonUtils.convertS2L(productIdsBySeller)));
                filter3.addClause(WhereClause.eq(Products.Fields.seller_id, seller.getId()));

                List<Products> randomProducts = productRepository.getRandomProducts(filter3, 7);

                ProductCarousel productCarousel = homeConfig.getProductCarousel();
                List<ProductBean> randomProductBeans = new ArrayList<>();
                for (Products randomProduct : randomProducts) {
                    long maxSellingPrize = highestPrize.getOrDefault(randomProduct.getProduct_master_id(), 0L);
                    if (maxSellingPrize > 0L) {
                        randomProduct.setSelling_price(maxSellingPrize);
                        randomProductBeans.add(getProductBean(randomProduct));
                    }
                }

                ProductCarouselBean productCarouselBean = ProductCarouselBean.builder()
                        .title(productCarousel.getTitle())
                        .subtitle(productCarousel.getSubtitle())
                        .products(randomProductBeans)
                        .build();

                homeProductsBeanBuilder.productCarousel(productCarouselBean);

                List<GroupComponent> groupComponent = homeConfig.getGroupComponent();

                List<GroupComponentBean> groupComponentBeans = new ArrayList<>();

                for (GroupComponent group : groupComponent) {
                    SEFilter filterPM = new SEFilter(SEFilterType.AND);
                    filterPM.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
                    filterPM.addClause(WhereClause.eq(Products.Fields.category_id, categoryId));
                    filterPM.addClause(WhereClause.eq(Products.Fields.group_id, group.getId()));
                    filterPM.addClause(WhereClause.isNotEmpty("media.cdn_url"));
                    filterPM.addClause(WhereClause.in(BaseMongoEntity.Fields.id, CommonUtils.convertS2L(productIdsBySeller)));
                    filterPM.addClause(WhereClause.eq(Products.Fields.seller_id, seller.getId()));
                    if (group.getFilters() != null && !group.getFilters().isEmpty()) {
                        for (Map.Entry<String, List<String>> entry : group.getFilters().entrySet()) {
                            if (StringUtils.hasText(entry.getKey()) && !CollectionUtils.isEmpty(entry.getValue())) {
                                Map<String, Object> map = new HashMap<>();
                                map.put(SelectedSubCategories.Fields.sub_category, entry.getKey());
                                map.put(SelectedSubCategories.Fields.selected_attributes, entry.getValue());
                                filterPM.addClause(WhereClause.elem_match(Products.Fields.selected_sub_catagories, map));
                            }
                        }
                    }

                    List<Products> productsByGroup = productRepository.getRandomProducts(filterPM, 7);

                    List<ProductBean> productListByGroup = new ArrayList<>();
                    for (Products productByGroup : productsByGroup) {
                        long maxSellingPrize = highestPrize.getOrDefault(productByGroup.getProduct_master_id(), 0L);
                        if (maxSellingPrize > 0L) {
                            productByGroup.setSelling_price(maxSellingPrize);
                            productListByGroup.add(getProductBean(productByGroup));
                        }
                    }


                    GroupComponentBean groupComponentBean = GroupComponentBean.builder()
                            .groupId(group.getId())
                            .title(group.getTitle())
                            .filters(group.getFilters())
                            .products(productListByGroup)
                            .build();
                    groupComponentBeans.add(groupComponentBean);
                }
                HomeProductsBean homeProductsBean = homeProductsBeanBuilder.groupComponent(groupComponentBeans)
                        .combo(false)
                        .build();
                homeProductsBeans.add(homeProductsBean);
            }
        }

        List<PromoBanners> promoBanners = new ArrayList<>();

        SEFilter filter4 = new SEFilter(SEFilterType.AND);
        filter4.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
        filter4.addClause(WhereClause.eq(AssetsEntity.Fields.type, AssetType.HOME_PROMO_BANNER.name()));

        List<AssetsEntity> assetsEntities = assetsService.repoFind(filter4);
        if (!CollectionUtils.isEmpty(assetsEntities)) {
            List<AssetsEntity> entities = assetsEntities.stream().sorted(Comparator.comparing(AssetsEntity::getOrder)).toList();
            for (AssetsEntity assetsEntity : entities) {
                promoBanners.add(PromoBanners.builder()
                        .url(assetsEntity.getUrl())
                        .order(assetsEntity.getOrder())
                        .altText(assetsEntity.getAltText())
                        .mobileView(assetsEntity.isMobileView())
                        .build());
            }
        }

        List<ProductBean> comboBeans = new ArrayList<>();

        SEFilter filterC = new SEFilter(SEFilterType.AND);
        filterC.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

        List<Combo> combos = comboService.repoFind(filterC);
        if (!CollectionUtils.isEmpty(combos)) {
            Combo combo = combos.get(0);
            boolean valid = comboUtility.validateCombo(combo);
            if (valid) {
                List<Products> comboProducts = comboUtility.getProductsByCombo(combo);
                long averageQuantity = products.stream().map(Products::getQuantity).toList().stream().sorted().toList().get(0);
                ProductBean productBean = ProductBean.builder()
                        .id(combo.getId())
                        .name(combo.getName())
                        .secure(false)
                        .image(!CollectionUtils.isEmpty(combo.getMedia()) ? combo.getMedia().get(0).getCdn_url() : comboProducts.stream().anyMatch(p -> !CollectionUtils.isEmpty(p.getMedia())) ? comboProducts.stream().filter(p -> !CollectionUtils.isEmpty(p.getMedia())).findFirst().get().getMedia().get(0).getCdn_url() : "")
                        .mrp(CommonUtils.paiseToRupee(combo.getMrp()))
                        .sellingPrice(CommonUtils.paiseToRupee(combo.getSelling_price()))
                        .quantity(averageQuantity)
                        .build();
                comboBeans.add(productBean);
            }
//            HomeProductsBean homeProductsBean = HomeProductsBean.builder()
//                    .combo(true)
//                    .mainBadge("Best Seller Combo")
//                    .mainTitle("Engineering Starter Pack")
//                    .mainSubtitle("Get all essentials in one bundle")
//                    .productCarousel(ProductCarouselBean.builder()
//                            .title("Included in this Combo")
//                            .subtitle("Handpicked books to kickstart your semester")
//                            .products(comboBeans)
//                            .build())
//                    .build();
//            homeProductsBeans.add(homeProductsBean);
        }

        Assets assets = Assets.builder()
                .homePromoBanners(promoBanners)
                .build();

        List<Category_Master> categoryMasterData = categoryFilterService.getFilters();

        return Config.builder()
                .categories(categoryMasterData)
                .homeProducts(homeProductsBeans)
                .assets(assets)
                .build();


    }

    private ProductBean getProductBean(Products product) {
        return ProductBean.builder()
                .mrp(CommonUtils.paiseToRupee(product.getMrp()))
                .sellingPrice(CommonUtils.paiseToRupee(product.getSelling_price()))
                .image(CollectionUtils.isEmpty(product.getMedia()) ? "" : product.getMedia().stream().filter(e -> e.getOrder() == 0).findFirst().get().getCdn_url())
                .id(product.getId())
                .name(product.getName())
                .quantity(product.getQuantity())
                .secure(product.getIs_secure())
                .build();
    }

    private ProductBean getProductBean(Product_Master product) {
        return ProductBean.builder()
                .mrp(CommonUtils.paiseToRupee(product.getMrp()))
                .sellingPrice(CommonUtils.paiseToRupee(product.getMrp()))
                .image(product.getCdn_url())
                .id(product.getId())
                .productMasterId(product.getId())
                .name(product.getName())
                .quantity(0L)
                .secure(false)
                .build();
    }

}
