package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubmissionRequest {
    String status;

    @JsonProperty("submission_code")
    @NotBlank(message = "Submitted code can't be empty")
    @Size(max = 8000,message = "Submission code cannot exceed 8000 characters")
    String submissionCode;

    @NotBlank(message = "Language can't be empty")
    String language;

    @JsonProperty("problem_id")
    @NotNull(message = "Problem id must be required")
    Long problemId;
}
