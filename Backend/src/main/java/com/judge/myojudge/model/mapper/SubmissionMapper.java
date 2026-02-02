package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.SubmissionResponse;
import com.judge.myojudge.model.entity.Submission;

public interface SubmissionMapper {

    SubmissionResponse toSubmissionResponse(Submission submission);
    Submission toSubmission(SubmissionResponse submissionResponse);

    SubmissionResponse toSubmissionResponseWithCode(Submission submission);
}
