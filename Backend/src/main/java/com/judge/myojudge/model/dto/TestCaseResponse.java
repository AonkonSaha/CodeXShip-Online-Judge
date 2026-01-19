package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseResponse {

    @JsonProperty("submission_id")
    private Long submissionId;
    private String status;
    private String stdout;
    @JsonProperty("expected_output")
    private String expectedOutput;
    private String stderr;

    @JsonProperty("compile_output")
    private String compileOutput;
    private String message;
    private String time;
    private String memory;
    private boolean passed;
   @JsonProperty("testcase_index")
    private Integer testcaseIndex;
    private Boolean completed=false;

}
