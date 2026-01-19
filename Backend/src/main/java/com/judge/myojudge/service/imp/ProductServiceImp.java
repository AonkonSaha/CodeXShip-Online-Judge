package com.judge.myojudge.service.imp;

import com.judge.myojudge.enums.OrderStatus;
import com.judge.myojudge.exception.UserNotFoundException;
import com.judge.myojudge.model.entity.Order;
import com.judge.myojudge.model.entity.Product;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.repository.OrderRepo;
import com.judge.myojudge.repository.ProductRepo;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.service.CloudinaryService;
import com.judge.myojudge.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {
   private final ProductRepo productRepo;
   private final UserRepo userRepo;
   private final OrderRepo orderRepo;
   private final CloudinaryService cloudinaryService;
    @Override
    public void addProduct(Product product, MultipartFile imageFile) throws Exception {
        if (imageFile==null){
            throw new IllegalArgumentException("Image couldn't found");
        }
        Map imageUploadedData = cloudinaryService.uploadImage(imageFile);
        product.setImageUrl(imageUploadedData.get("secure_url").toString());
        product.setImageFileKey(imageUploadedData.get("public_id").toString());
        productRepo.save(product);
    }

    @Override
    @Transactional
    public void buyProduct(Long id) {
    String mobileOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = null;
    if(mobileOrEmail.contains("@")){
        user = userRepo.findByEmail(mobileOrEmail).orElseThrow(()-> new UserNotFoundException("User not found"));
    }else{
        user = userRepo.findByMobileNumber(mobileOrEmail).orElseThrow(()-> new UserNotFoundException("User not found"));
    }
    Product product = productRepo.findById(id).orElseThrow(()->new IllegalArgumentException("Product not found"));
    if(user.getTotalPresentCoins()<product.getCoins()){
        throw new IllegalArgumentException("Insufficient coins");
    }
    Order order = Order.builder()
            .status(OrderStatus.CONFIRMED.name())
            .user(user)
            .product(product)
            .build();
        if (user.getOrder() == null) {
            user.setOrder(Set.of(order));
        } else {
            user.getOrder().add(order);
        }
        user.setTotalCoinsExpend(user.getTotalCoinsExpend()==null?product.getCoins():user.getTotalCoinsExpend()+product.getCoins());
        user.setTotalPresentCoins(user.getTotalPresentCoins()-product.getCoins());
        orderRepo.save(order);
    }

    @Override
    public Page<Product> getProduct(String search, Pageable pageable) {
        return productRepo.getAllWithFilter(search, pageable);
    }

    @Override
    public Page<Order> getOderDetails(String search, Pageable pageable) {
        return orderRepo.getAllWithFilter(search,pageable);
    }

    @Override
    @Transactional
    public void declineOrder(Long id) {
        Order order = orderRepo.findById(id).orElseThrow(()->new IllegalArgumentException("Order not found"));
        if(!order.getStatus().equals(OrderStatus.DECLINED.name())){
            order.getUser().setTotalPresentCoins(order.getUser().getTotalPresentCoins()==null?
                    order.getProduct().getCoins():
                    order.getUser().getTotalPresentCoins()+order.getProduct().getCoins());
            order.getUser().setTotalCoinsExpend(order.getUser().getTotalCoinsExpend()==null?
                    order.getProduct().getCoins():order.getUser().getTotalCoinsExpend()-order.getProduct().getCoins());
        }

        order.setStatus(OrderStatus.DECLINED.name());
        orderRepo.save(order);
    }

    @Override
    @Transactional
    public void markedShipped(Long id) {
        Order order = orderRepo.findById(id).orElseThrow(()->new IllegalArgumentException("Order not found"));

        if(order.getStatus().equals(OrderStatus.DECLINED.name())){
            if(order.getUser().getTotalPresentCoins()==null || order.getUser().getTotalPresentCoins()<order.getProduct().getCoins()){
                throw new IllegalArgumentException("Insufficient coins");
            }
            order.getUser().setTotalPresentCoins(order.getUser().getTotalPresentCoins()-order.getProduct().getCoins());
            order.getUser().setTotalCoinsExpend(order.getUser().getTotalCoinsExpend()==null?
                    order.getProduct().getCoins():order.getUser().getTotalCoinsExpend()+order.getProduct().getCoins());
        }
        order.setStatus(OrderStatus.SHIPPED.name());
        orderRepo.save(order);
    }

    @Override
    @Transactional
    public Page<Order> getOderDetailsByUser(String search, Pageable pageable) {
        String mobileOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderRepo.getOrderByMobileOrEmail(mobileOrEmail,search,pageable);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepo.findById(id).orElseThrow(()->new IllegalArgumentException("Order not found"));
        if(!order.getStatus().equals(OrderStatus.DECLINED.name())){
            order.getUser().setTotalPresentCoins(order.getUser().getTotalPresentCoins()+order.getProduct().getCoins());
            order.getUser().setTotalCoinsExpend(order.getUser().getTotalCoinsExpend()+order.getProduct().getCoins());
        }
        orderRepo.deleteById(id);
    }

}
