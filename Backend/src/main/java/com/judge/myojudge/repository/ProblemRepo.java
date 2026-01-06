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
            "WHERE LOWER(TRIM(p.type)) = :type " +
            "AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:difficulty IS NULL OR LOWER(p.difficulty) LIKE CONCAT('%', :difficulty, '%')) " +
            "AND (:solvedFilter Is NULL OR 'unsolved' Like concat('%', : solvedFilter, '%')) " +
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


    @Query("SELECT DISTINCT  p, CASE WHEN EXISTS " +
            "(SELECT s FROM Submission s WHERE s.problem.id = p.id " +
            "AND (s.user.mobileNumber = :mobileOrEmail " +
            "OR s.user.email = :mobileOrEmail) " +
            "AND LOWER(TRIM(s.status)) = 'accepted' ) THEN 'solved' ELSE 'unsolved'  END AS solvedStatus " +
            "FROM Problem p "+
            "WHERE lower(trim(p.type))= :category " +
            "AND (:search IS NULL OR (lower(trim(p.title)) Like LOWER(CONCAT('%', :search, '%'))) OR (lower(trim(p.type)) Like LOWER(CONCAT('%', :search, '%')))) " +
            "AND (" +
            " :solvedFilter IS NULL OR :solvedFilter = '' OR " +
            " (LOWER(CASE WHEN EXISTS (" +
            "     SELECT s2 FROM Submission s2 WHERE s2.problem.id = p.id " +
            "     AND (s2.user.mobileNumber = :mobileOrEmail OR s2.user.email = :mobileOrEmail) " +
            "     AND LOWER(TRIM(s2.status)) = 'accepted')" +
            "  THEN 'solved' ELSE 'unsolved' END) = LOWER(:solvedFilter)) " +
            ") "+
            "AND (:difficulty IS NULL OR lower(trim(p.difficulty)) Like concat('%',:difficulty,'%')) "+
            "order by p.id ASC")
    Page<Object[]> findByCategoryWithSolvedOrNotFilter(
            @Param("mobileOrEmail") String mobileOrEmail,
            @Param("category") String category,
            @Param("search") String search,
            @Param("difficulty") String difficulty,
            @Param("solvedFilter") String solvedFilter,
            Pageable pageable);


    @Query("SELECT p,(" +
            "SELECT COUNT(p2)>0 FROM Problem p2 left join p2.submissions s " +
            "ON s.problem.id=p2.id " +
            "AND (s.user.mobileNumber=:mobileOrEmail OR s.user.email=:mobileOrEmail) " +
            "AND s.problem.id=:problemId " +
            "WHERE lower(s.status)='accepted') FROM Problem p WHERE p.id=:problemId")
    List<Object[]> findProblemByStatus(@Param("problemId") Long problemId,
                                       @Param("mobileOrEmail") String mobileOrEmail);
}
