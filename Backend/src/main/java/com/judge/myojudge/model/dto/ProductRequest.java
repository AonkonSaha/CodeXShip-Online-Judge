package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private Long id;
    @NotBlank(message = "Product can't empty")
    private String title;
    @JsonProperty("image_url")
    @NotBlank(message = "Please upload product image")
    private String imageUrl;
    @NotBlank(message = "Product type can't empty")
    private String type;
    @NotNull(message="Price can't empty")
    private Long price;
    private Long quantity;
    @NotNull(message="Coins can't empty")
    private Long coins;
    private String description;
}
