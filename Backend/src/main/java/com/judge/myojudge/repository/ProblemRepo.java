package com.judge.myojudge.repository;

import com.judge.myojudge.model.entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemRepo extends JpaRepository<Problem,Long> {
    Optional<Problem> findByHandleName(String handle);
    boolean existsByHandleName(String handle);
    @Query("SELECT p FROM Problem p " +
            "WHERE p.type = :type AND (" +
            "(:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            ")" +
            "AND (:difficulty IS NULL OR LOWER(p.difficulty) LIKE LOWER(CONCAT('%', :difficulty, '%')))" +
            ") " +
            "ORDER BY p.id ASC")
    Page<Problem> findByType(
            @Param("type") String type,
            @Param("search") String search,
            @Param("difficulty") String difficulty,
            Pageable pageable);
    boolean existsByTitle(String title);
}
