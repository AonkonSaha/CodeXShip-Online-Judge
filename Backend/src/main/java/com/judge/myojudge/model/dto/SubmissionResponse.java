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
public class SubmissionResponse {
    private Long id;
    private String problemName;
    private int total;
    private int passed;
    private String verdict;
    private float time;
    private Long memory;
    List<TestcaseResultDTO> results;
}
