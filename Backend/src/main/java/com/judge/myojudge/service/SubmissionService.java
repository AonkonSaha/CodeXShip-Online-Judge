package com.judge.myojudge.service;

import com.judge.myojudge.model.dto.BatchRequest;
import com.judge.myojudge.model.dto.BatchResponse;
import com.judge.myojudge.model.dto.SubmissionRequest;
import com.judge.myojudge.model.dto.SubmissionResponse;

public interface SubmissionService {

    public SubmissionResponse excuteCode(SubmissionRequest submissionRequest);
}
