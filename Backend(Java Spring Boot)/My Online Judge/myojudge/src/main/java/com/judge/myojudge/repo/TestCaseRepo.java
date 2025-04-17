package com.judge.myojudge.repo;

import com.judge.myojudge.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestCaseRepo extends JpaRepository<TestCase,Long> {


    Optional<TestCase> findByHandle(String handle);
}
