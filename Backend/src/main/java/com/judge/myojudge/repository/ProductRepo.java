package com.judge.myojudge.repository;

import com.judge.myojudge.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product,Long> {


    @Query("SELECT p FROM Product p " +
            "WHERE (:search IS NULL OR :search = '' OR LOWER(TRIM(p.type)) = LOWER(TRIM(:search))) " +
            "ORDER BY p.id ASC")
    Page<Product> getAllWithFilter(@Param("search") String search, Pageable pageable);
}
