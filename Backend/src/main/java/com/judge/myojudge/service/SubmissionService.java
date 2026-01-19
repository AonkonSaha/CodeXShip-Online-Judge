package com.judge.myojudge.service;

import com.judge.myojudge.model.dto.SubmissionRequest;
import com.judge.myojudge.model.dto.SubmissionResponse;
import com.judge.myojudge.model.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.concurrent.ExecutionException;

public interface SubmissionService {
    Submission getSubmission(SubmissionRequest submissionRequest,String mobileOrEmail);
    void runSubmissionCode(SubmissionRequest submissionRequest, Submission submission, String mobileOrEmail) throws ExecutionException, InterruptedException;
    SubmissionResponse runSampleTestCaseCode(SubmissionRequest submissionRequest);
    Page<SubmissionResponse> getAllSubmissionByUser(String mobileOrEmail,String search, Sort sort, int page, int size);

    void deleteAllSubmissionByUser(String mobileOrEmail);
}