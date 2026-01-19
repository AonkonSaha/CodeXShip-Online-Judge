package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProblemSampleTestCaseResponse {

    private Long id;
    private String handle;
    private String title;
    private String problemStatement;
    private String explanation;
    private String difficulty;
    private String type;
    @JsonProperty("is_solved")
    private boolean isSolved=false;
    private Long coins;
    @JsonProperty("time_limit")
    private double timeLimit;
    @JsonProperty("memory_limit")
    private double memoryLimit;
    private List<String> sampleTestcase;
    private List<String> sampleOutput;
}
