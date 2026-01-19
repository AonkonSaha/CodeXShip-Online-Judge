package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.OrderRequest;
import com.judge.myojudge.model.dto.ProductRequest;
import com.judge.myojudge.model.entity.Order;
import com.judge.myojudge.model.entity.Product;

import java.util.List;
public interface ProductMapper {

    Product toProduct(String title, String type, Long price, Long coins, String description);
    List<ProductRequest> toProductDTOS(List<Product> productList);

    List<OrderRequest> toOrderDTOs(List<Order> oderDetails);
}
