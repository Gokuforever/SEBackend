package com.sorted.commons.beans;

import com.sorted.commons.entity.mongo.Category_Master;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Config {
    private List<Category_Master> categories;
    private List<HomeProductsBean> homeProducts;
    private Assets assets;
    private boolean isLocationServiceable;
}
