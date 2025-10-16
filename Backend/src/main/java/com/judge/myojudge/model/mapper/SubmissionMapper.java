package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.SubmissionResponse;
import com.judge.myojudge.model.entity.Submission;

public interface SubmissionMapper {

    public SubmissionResponse toSubmissionResponse(Submission submission);
    public Submission toSubmission(SubmissionResponse submissionResponse);
}
