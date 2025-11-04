package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.OrderDTO;
import com.judge.myojudge.model.dto.ProductDTO;
import com.judge.myojudge.model.entity.Order;
import com.judge.myojudge.model.entity.Product;
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
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
   private final ProductService productService;
   private final ProductMapper productMapper;
    @PostMapping("/v1/add")
    public ResponseEntity<ApiResponse<String>> addProduct(
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
    @GetMapping("v1/get")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Product> products = productService.getProduct(search,pageable);
        List<ProductDTO> productDTOS = productMapper.toProductDTOS(products.getContent());
        return ResponseEntity.ok(
                ApiResponse.<Page<ProductDTO>>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Product Fetch Successfully")
                        .data(new PageImpl<>(productDTOS,pageable,products.getTotalElements()))
                        .build());

    }
    @PostMapping("/v1/buy/{id}")
    public ResponseEntity<ApiResponse<String>> buyProduct(@PathVariable Long id) {
        productService.buyProduct(id);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully buy product")
                        .data("Successfully buy product")
                        .build());
    }

    @GetMapping("/v1/history/order/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search
    ){
        Pageable pageable = PageRequest.of(page,size);
        Page<Order> orders = productService.getOderDetails(search,pageable);
        List<OrderDTO> products = productMapper.toOrderDTOs(orders.getContent());
        return ResponseEntity.ok(
                ApiResponse.<Page<OrderDTO>>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Product Fetch Successfully")
                        .data(new PageImpl<>(products,pageable,orders.getTotalElements()))
                        .build());
    }

    @GetMapping("/v1/history/order")
    @PreAuthorize("hasAnyRole('ADMIN','NORMAL_USER')")
    @Transactional
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getProductOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search
    ){
        Pageable pageable = PageRequest.of(page,size);
        Page<Order> orders = productService.getOderDetailsByUser(search,pageable);
        List<OrderDTO> products = productMapper.toOrderDTOs(orders.getContent());
        return ResponseEntity.ok(
                ApiResponse.<Page<OrderDTO>>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Product Fetch Successfully")
                        .data(new PageImpl<>(products,pageable,orders.getTotalElements()))
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/v1/order/{action}/{id}")
    public ResponseEntity<Void> declineProductOrder(@PathVariable Long id,
                                               @PathVariable String action) {
        if(action.equals("decline")) {
            productService.declineOrder(id);
        }else{
            productService.markedShipped(id);
        }
        return ResponseEntity.noContent().build();
    }


}
