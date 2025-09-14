package com.judge.myojudge.repository;

import com.judge.myojudge.model.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepo extends JpaRepository<Problem,Long> {
    Problem findByHandleName(String handle);
    boolean existsByHandleName(String handle);
    List<Problem> findByType(String type);
}
