package com.judge.myojudge.service;

import com.judge.myojudge.model.dto.SubmissionRequest;
import com.judge.myojudge.model.dto.SubmissionResponse;

import java.util.List;
import java.util.Set;

public interface SubmissionService {

    public SubmissionResponse excuteCode(SubmissionRequest submissionRequest);

    Set<SubmissionResponse> getAllSubmissionByUser(String contact);
}
