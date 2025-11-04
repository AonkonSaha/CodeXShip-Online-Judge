package com.judge.myojudge.repository;

import com.judge.myojudge.model.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SubmissionRepo extends JpaRepository<Submission,Long> {

    @Query("SELECT s FROM Submission s " +
            "WHERE (s.user.mobileNumber = :mobileOrEmail OR s.user.email = :mobileOrEmail) " +
            "AND (:search IS NULL OR " +
            "     LOWER(s.problem.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(s.status) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     CAST(s.createdAt AS string) LIKE CONCAT('%', :search, '%') OR " +
            "     CAST(s.passedTestcases AS string) LIKE CONCAT('%', :search, '%') OR " +
            "     CAST(s.totalTestcases AS string) LIKE CONCAT('%', :search, '%') OR " +
            "     CAST(s.time AS string) LIKE CONCAT('%', :search, '%') OR " +
            "     CAST(s.memory AS string) LIKE CONCAT('%', :search, '%')" +
            ")")
    Page<Submission> findSubmissionsByMobileOrEmail(
            @Param("mobileOrEmail") String mobileOrEmail,
            @Param("search") String search,
            Pageable pageable);

   @Query("SELECT s FROM Submission s WHERE (s.user.mobileNumber = :mobileOrEmail OR s.user.email = :mobileOrEmail) AND lower(s.handle) = lower(:handleName) AND lower(s.status) = lower(:accepted)")
   List<Submission> findByMobileOrEmailAndHandleAndStatus(@Param("mobileOrEmail") String mobileOrEmail,
                                              @Param("handleName") String handleName,
                                              @Param("accepted") String accepted);
    @Query("SELECT s FROM Submission s WHERE s.user.email = :email AND lower(s.status) = lower(:accepted)")
    List<Submission> findAllSubmissionByEmailAndStatus(@Param("email") String email,
                                                     @Param("accepted") String accepted);


}
