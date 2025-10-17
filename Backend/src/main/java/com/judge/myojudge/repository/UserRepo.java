package com.judge.myojudge.repository;

import com.judge.myojudge.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByMobileNumber(String mobileNumber);

    boolean existsByMobileNumber(String mobileNumber);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles WHERE u.mobileNumber = :mobileNumber")
    Optional<User> findByMobileNumberWithRoles(@Param("mobileNumber") String mobileNumber);

    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM musers u " +
            "WHERE (:search IS NULL OR :search = '' OR LOWER(u.user_name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY (COALESCE(u.total_problems_solved,0) + COALESCE(u.total_present_coins,0)) DESC",
            nativeQuery = true)
    Page<User> findAllUserByRank(@Param("search") String search, Pageable pageable);
}

