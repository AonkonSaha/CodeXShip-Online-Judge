package com.judge.myojudge.repository;

import com.judge.myojudge.model.entity.User;
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

    @Query("SELECT u FROM User u order by (u.totalProblemsSolved+u.TotalPresentCoins) ASC")
    List<User> findAllUserByRank();
}

