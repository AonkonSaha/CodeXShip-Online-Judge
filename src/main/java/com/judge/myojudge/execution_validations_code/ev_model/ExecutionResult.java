package com.judge.myojudge.execution_validations_code.ev_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResult {
    private Long problemId;
    private List<TestCaseResult> results;
}
