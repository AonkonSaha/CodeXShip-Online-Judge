package com.judge.myojudge.controller;

import com.judge.myojudge.model.mapper.ProductMapper;
import com.judge.myojudge.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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


}
