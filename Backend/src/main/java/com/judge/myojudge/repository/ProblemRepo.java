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
            "WHERE (:type IS NULL OR :type = '' OR LOWER(TRIM(p.type)) = LOWER(TRIM(:type))) " +
            "ORDER BY p.id ASC")
    Page<Problem> findByCategoryORFilter(
            @Param("type") String type,
            @Param("search") String search,
            @Param("difficulty") String difficulty,
            Pageable pageable);

    boolean existsByTitle(String title);
@Query("SELECT p FROM Problem p where lower(p.type)=lower(:category) ")
    List<Problem> findByType(@Param("category") String category);
}
