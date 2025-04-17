package com.judge.myojudge.execution_validations_code.ev_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseResult {
    private String testcaseName;
    private Boolean  isPassed;
}
