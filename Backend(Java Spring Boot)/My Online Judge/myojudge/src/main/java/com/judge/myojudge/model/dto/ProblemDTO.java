package com.judge.myojudge.model.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.judge.myojudge.model.entity.TestCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemDTO {
    private Long id;
    private String handle;
    private String title;
    @JsonProperty("problem_statement")
    private String problemStatement;
    private String difficulty;
    private String type;
    private List<TestCase> testcases;


}
