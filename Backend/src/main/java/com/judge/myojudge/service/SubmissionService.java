package com.judge.myojudge.service;

import com.judge.myojudge.model.dto.SubmissionRequest;
import com.judge.myojudge.model.dto.SubmissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public interface SubmissionService {

    public SubmissionResponse excuteCode(SubmissionRequest submissionRequest);

    Page<SubmissionResponse> getAllSubmissionByUser(String contact,String search, Sort sort, int page, int size);
}
