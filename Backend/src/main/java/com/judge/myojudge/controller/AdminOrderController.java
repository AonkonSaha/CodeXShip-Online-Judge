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
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping("/{action}/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> declineProductOrder(@PathVariable Long id,
                                                    @PathVariable String action) {
        if(action.equals("decline")) {
            productService.declineOrder(id);
        }else{
            productService.markedShipped(id);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> declineProductOrder(@PathVariable Long id) {
        productService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<ApiResponse<Page<OrderRequest>>> getOrders(
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
