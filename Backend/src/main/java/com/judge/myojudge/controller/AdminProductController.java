package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.OrderRequest;
import com.judge.myojudge.model.entity.Order;
import com.judge.myojudge.model.mapper.ProductMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN','PROBLEM_EDITOR')")
    public ResponseEntity<ApiResponse<String>> createProduct(
            @RequestParam("title") String title,
            @RequestParam("type") String type,
            @RequestParam("price") Long price,
            @RequestParam("coins") Long coins,
            @RequestParam("description") String description,
            @RequestParam("image_file") MultipartFile imageFile) throws Exception {
        productService.addProduct(productMapper.toProduct(title,type,price,coins,description),imageFile);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .statusCode(200)
                .message("Product Added Successfully")
                .data("Product Added Successfully")
                .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<ApiResponse<Page<OrderRequest>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search
    ){
        Pageable pageable = PageRequest.of(page,size);
        Page<Order> orders = productService.getOderDetails(search,pageable);
        List<OrderRequest> products = productMapper.toOrderDTOs(orders.getContent());
        return ResponseEntity.ok(
                ApiResponse.<Page<OrderRequest>>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Product Fetch Successfully")
                        .data(new PageImpl<>(products,pageable,orders.getTotalElements()))
                        .build());
    }
}
