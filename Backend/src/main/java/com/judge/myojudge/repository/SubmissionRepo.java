package com.judge.myojudge.repository;

import com.judge.myojudge.model.dto.SubmissionResponse;
import com.judge.myojudge.model.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SubmissionRepo extends JpaRepository<Submission,Long> {

    @Query("SELECT s FROM Submission s WHERE s.user.mobileNumber = :contact")
    Page<Submission> findSubmissionsByContact(@Param("contact") String contact, Pageable pageable);

}
