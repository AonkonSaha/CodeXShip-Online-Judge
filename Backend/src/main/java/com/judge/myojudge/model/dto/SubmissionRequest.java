package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SubmissionRequest {
    String status;
    @JsonProperty("submission_code")
    String submissionCode;
    String language;
    @JsonProperty("problem_id")
    Long problemId;
}
