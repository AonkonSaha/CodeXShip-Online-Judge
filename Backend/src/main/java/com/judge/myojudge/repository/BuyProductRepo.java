package com.judge.myojudge.repository;

import com.judge.myojudge.model.entity.BuyProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyProductRepo extends JpaRepository<BuyProduct,Long> {
}
