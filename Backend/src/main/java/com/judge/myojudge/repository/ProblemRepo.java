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
            "WHERE LOWER(TRIM(p.type)) = LOWER(TRIM(:type)) " +
            "AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:difficulty IS NULL OR LOWER(p.difficulty) LIKE LOWER(CONCAT('%', :difficulty, '%'))) " +
            "AND (:solvedFilter Is NULL OR LOWER('Unsolved') Like LOWER(concat('%', : solvedFilter, '%'))) " +
            "ORDER BY p.id ASC")
    Page<Problem> findByCategoryWithFilter(
            @Param("type") String type,
            @Param("search") String search,
            @Param("difficulty") String difficulty,
            @Param("solvedFilter") String solvedFilter,
            Pageable pageable);

    boolean existsByTitle(String title);

@Query("SELECT p FROM Problem p where lower(p.type)=lower(:category) ")
    List<Problem> findByType(@Param("category") String category);


    @Query("SELECT DISTINCT p FROM Problem p JOIN p.submissions s ON p.id = s.problem.id " +
            "WHERE (" +
            ":solvedFilter IS NULL OR ((lower(trim(CASE when lower(trim(s.status)) = 'accepted' THEN 'solved' else 'unsolved' end))) = lower(trim(:solvedFilter)))) " +
            "AND lower(trim(p.type))= lower(trim(:category)) " +
            "AND (p.user.mobileNumber = :mobileOrEmail OR p.user.email = : mobileOrEmail) " +
            "AND (:search IS NULL OR ((lower(trim(p.title)) Like concat('%',lower(trim(:search)),'%')) " +
            "OR (lower(trim(p.type)) Like concat('%', lower(trim(:search)),'%')))) " +
            "AND (:difficulty IS NULL OR lower(trim(p.difficulty)) Like concat('%',lower(trim(:difficulty)),'%')) "+
            "order by p.id ASC")
    Page<Problem> findByCategoryWithSolvedOrNotFilter(@Param("mobileOrEmail") String mobileOrEmail,
                                          @Param("category") String category,
                                          @Param("search") String search,
                                          @Param("difficulty") String difficulty,
                                          @Param("solvedFilter") String solvedFilter,
                                          Pageable pageable);
}
