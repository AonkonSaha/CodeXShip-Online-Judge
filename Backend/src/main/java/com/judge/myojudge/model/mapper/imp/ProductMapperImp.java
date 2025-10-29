package com.judge.myojudge.model.mapper.imp;

import com.judge.myojudge.model.dto.OrderDTO;
import com.judge.myojudge.model.dto.ProductDTO;
import com.judge.myojudge.model.entity.Order;
import com.judge.myojudge.model.entity.Product;
import com.judge.myojudge.model.mapper.ProductMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Data
@Component
@RequiredArgsConstructor
public class ProductMapperImp implements ProductMapper {


    @Override
    public Product toProduct(String title, String type, Long price, Long coins, String description) {
        return Product.builder()
                .title(title)
                .type(type)
                .description(description)
                .price(price)
                .coins(coins)
                .build();
    }

    @Override
    public List<ProductDTO> toProductDTOS(List<Product> productList) {
        List<ProductDTO> productDTOS=new ArrayList<>();
        for (Product product:productList) {
            productDTOS.add(
                    ProductDTO.builder()
                            .id(product.getId())
                            .title(product.getTitle())
                            .type(product.getType())
                            .price(product.getPrice())
                            .coins(product.getCoins())
                            .imageUrl(product.getImageUrl())
                            .description(product.getDescription())
                            .build());
        }
        return productDTOS;
    }

    @Override
    public List<OrderDTO> toOrderDTOs(List<Order> oderDetails) {
        System.out.println("Hello World I am here");
        List<OrderDTO> orderDTOS=new ArrayList<>();
        for (Order order:oderDetails) {
            orderDTOS.add(
                    OrderDTO.builder()
                            .orderId(order.getId())
                            .userId(order.getId())
                            .username(order.getUser().getUsername())
                            .mobile(order.getUser().getMobileNumber())
                            .country(order.getUser().getCountry())
                            .city(order.getUser().getCity())
                            .state(order.getUser().getState())
                            .postalCode(order.getUser().getPostalCode())
                            .productTitle(order.getProduct().getTitle())
                            .productType(order.getProduct().getType())
                            .coins(order.getProduct().getCoins())
                            .orderAt(order.getCreatedAt())
                            .deliveryAt(order.getDeliveryDate())
                            .status(order.getStatus())
                            .build()
            );}

        return orderDTOS;
    }
}
