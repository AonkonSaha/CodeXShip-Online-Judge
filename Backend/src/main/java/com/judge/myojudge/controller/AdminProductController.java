package com.judge.myojudge.controller;

import com.judge.myojudge.model.mapper.ProductMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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


}
