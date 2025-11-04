package com.judge.myojudge.repository;

import com.judge.myojudge.model.entity.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Order,Long> {

    @Transactional
    @Query("SELECT o FROM Order o " +
            "WHERE (:search IS NULL " +
            "OR :search = '' " +
            "OR LOWER(TRIM(o.user.username)) LIKE LOWER(concat('%',TRIM(:search),'%')) " +
            "OR LOWER(TRIM(o.product.type)) LIKE LOWER(concat('%',TRIM(:search),'%')) " +
            "OR LOWER(TRIM(o.product.title)) LIKE LOWER(concat('%',TRIM(:search),'%'))) " +
            "Order By o.createdAt DESC")
    Page<Order> getAllWithFilter(@Param("search") String search, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE (o.user.mobileNumber= :mobileOrEmail OR o.user.email = :mobileOrEmail) ORDER BY o.createdAt DESC")
    Page<Order> getOrderByMobileOrEmail(@Param("mobileOrEmail") String mobileOrEmail,
                                 @Param("search") String search,
                                 Pageable pageable);
}
