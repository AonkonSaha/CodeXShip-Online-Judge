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

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class UserOrderController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROBLEM_EDITOR',NORMAL_USER')")
    public ResponseEntity<ApiResponse<Void>> createOrder(@RequestParam("product_id") Long id) {
        productService.buyProduct(id);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .statusCode(HttpStatus.CREATED.value())
                .message("Product purchased successful")
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

    }
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROBLEM_EDITOR',NORMAL_USER')")
    @Transactional
    public ResponseEntity<ApiResponse<Page<OrderRequest>>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search
    ){
        Pageable pageable = PageRequest.of(page,size);
        Page<Order> orders = productService.getOderDetailsByUser(search,pageable);
        List<OrderRequest> products = productMapper.toOrderDTOs(orders.getContent());
        return ResponseEntity.ok(
                ApiResponse.<Page<OrderRequest>>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully Fetched Order Histories")
                        .data(new PageImpl<>(products,pageable,orders.getTotalElements()))
                        .build());
    }
}
