package com.judge.myojudge.model.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionResponse {
    private int total;
    private int passed;
    private String verdict;
    private float time;
    private Long memory;
    List<TestcaseResultDTO> results;
}
