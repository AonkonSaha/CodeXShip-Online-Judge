package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.ProductRequest;
import com.judge.myojudge.model.entity.Product;
import com.judge.myojudge.model.mapper.ProductMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class UserProductController {
   private final ProductService productService;
   private final ProductMapper productMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductRequest>>> getProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Product> products = productService.getProduct(search,pageable);
        List<ProductRequest> productRequests = productMapper.toProductDTOS(products.getContent());
        return ResponseEntity.ok(
                ApiResponse.<Page<ProductRequest>>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Product Fetch Successfully")
                        .data(new PageImpl<>(productRequests,pageable,products.getTotalElements()))
                        .build());

    }

}
