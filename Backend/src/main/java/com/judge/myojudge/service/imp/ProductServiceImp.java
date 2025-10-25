package com.judge.myojudge.service.imp;

import com.judge.myojudge.model.entity.BuyProduct;
import com.judge.myojudge.model.entity.Product;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.repository.BuyProductRepo;
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
   private final BuyProductRepo buyProductRepo;
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
    String contact = SecurityContextHolder.getContext().getAuthentication().getName();
    Product product = productRepo.findById(id).orElseThrow(()->new IllegalArgumentException("Product not found"));
    User user = userRepo.findByMobileNumber(contact).orElseThrow(()->new IllegalArgumentException("User not found"));
    if(user.getTotalPresentCoins()<product.getCoins()){
        throw new IllegalArgumentException("Insufficient coins");
    }
    BuyProduct buyProduct = BuyProduct.builder()
            .status("Confirm")
            .user(user)
            .product(product)
            .build();
        if (user.getBuyProduct() == null) {
            user.setBuyProduct(Set.of(buyProduct));
        } else {
            user.getBuyProduct().add(buyProduct);
        }
        buyProductRepo.save(buyProduct);
    }

    @Override
    public Page<Product> getProduct(String search, Pageable pageable) {
        return productRepo.getAllWithFilter(search, pageable);
    }
}
