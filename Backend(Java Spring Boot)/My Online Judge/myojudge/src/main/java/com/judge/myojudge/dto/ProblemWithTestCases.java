package com.judge.myojudge.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
@Builder
public class ProblemWithTestCases {

    private Long id;
    private String handle;
    private String title;
    private String problemStatement;
    private String difficulty;
    private String type;
    private List<String> sampleTestcase;
    private List<String> sampleOutput;

}
