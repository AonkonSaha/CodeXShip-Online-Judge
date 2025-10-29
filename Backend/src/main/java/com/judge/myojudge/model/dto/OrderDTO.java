package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    @JsonProperty("order_id")
    private Long orderId;
    @JsonProperty("user_id")
    private Long userId;
    private String username;
    private String mobile;
    private String country;
    private String city;
    private String state;
    @JsonProperty("postal_code")
    private String postalCode;
    @JsonProperty("product_title")
    private String productTitle;
    @JsonProperty("product_type")
    private String productType;
    @JsonProperty("num_of_products")
    private Long numOfProducts;
    private String status;
    private Long coins;
    @JsonProperty("order_at")
    private LocalDateTime orderAt;
    @JsonProperty("delivery_at")
    private LocalDateTime deliveryAt;
}
