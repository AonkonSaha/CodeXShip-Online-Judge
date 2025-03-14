package com.judge.myojudge.execution_validations_code.ev_repo;

import com.judge.myojudge.execution_validations_code.ev_model.CodeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeSubmissionRepository extends JpaRepository<CodeSubmission, Long> {

    List<CodeSubmission> findByUserName(String userName);
}

