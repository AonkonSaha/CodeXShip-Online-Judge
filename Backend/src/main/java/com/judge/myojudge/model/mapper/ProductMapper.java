package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.ProductDTO;
import com.judge.myojudge.model.entity.Product;

import java.util.List;
public interface ProductMapper {

    Product toProduct(String title, String type, Long price, Long coins, String description);
    List<ProductDTO> toProductDTOS(List<Product> productList);
}
