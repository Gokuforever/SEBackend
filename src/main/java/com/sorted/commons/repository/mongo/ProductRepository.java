package com.sorted.commons.repository.mongo;

import com.sorted.commons.entity.mongo.Products;
import com.sorted.commons.helper.AggregationFilter;
import com.sorted.commons.helper.BaseMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends BaseMongoRepository<String, Products> {

    @Override
    default Class<Products> getEntityType() {
        return Products.class;
    }

    default List<Products> getRandomProducts(AggregationFilter.SEFilter f, long count) {
        return this.random(f, count);
    }

}
