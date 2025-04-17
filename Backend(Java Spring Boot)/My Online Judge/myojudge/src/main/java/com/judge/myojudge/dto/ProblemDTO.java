package com.judge.myojudge.dto;
import com.judge.myojudge.model.TestCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class ProblemDTO {
    private Long id;
    private String handle;
    private String title;
    private String problemStatement;
    private String difficulty;
    private String type;
    private List<TestCase> testcases;


}
