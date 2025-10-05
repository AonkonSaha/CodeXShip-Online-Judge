package com.judge.myojudge.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProblemWithSample {

    private Long id;
    private String handle;
    private String title;
    private String problemStatement;
    private String difficulty;
    private String type;
    private Long coins;
    private List<String> sampleTestcase;
    private List<String> sampleOutput;
}
