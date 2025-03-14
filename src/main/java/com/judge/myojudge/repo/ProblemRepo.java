package com.judge.myojudge.repo;

import com.judge.myojudge.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepo extends JpaRepository<Problem,Long> {
    Problem findByHandle(String handle);
    boolean existsByHandle(String handle);
    List<Problem> findByType(String type);
}
