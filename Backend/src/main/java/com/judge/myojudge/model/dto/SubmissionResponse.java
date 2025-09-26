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
    List<TestcaseResult> results;
}
