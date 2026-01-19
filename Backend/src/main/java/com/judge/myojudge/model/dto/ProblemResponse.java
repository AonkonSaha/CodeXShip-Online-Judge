package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProblemResponse {
    private Long id;
    private String title;
    private String handle;
    private String difficulty;
    private String type;
    @JsonProperty("problem_statement")
    private String problemStatement;
    private Map<String,String> testCaseNameWithPath = new HashMap<>();
    private Long coins;
    private String explanation;
    @JsonProperty("is_solved")
    private boolean isSolved=false;
    @JsonProperty("time_limit")
    private double timeLimit;
    @JsonProperty("memory_limit")
    private double memoryLimit;

}
