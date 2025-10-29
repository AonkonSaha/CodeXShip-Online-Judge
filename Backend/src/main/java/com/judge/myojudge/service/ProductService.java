package com.judge.myojudge.service;

import com.judge.myojudge.model.entity.Order;
import com.judge.myojudge.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    public void addProduct(Product product, MultipartFile imageFile) throws Exception;
    public void buyProduct(Long id);

    Page<Product> getProduct(String search, Pageable pageable);
    List<Order> getOderDetails(String search, Pageable pageable);

    void declineOrder(Long id);

    void markedShipped(Long id);
}
