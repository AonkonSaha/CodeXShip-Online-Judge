package com.judge.myojudge.model.dto;

import com.judge.myojudge.model.entity.TestCase;
import java.util.List;
public class BatchRequest {

    public Integer languageId;
    public String sourceCode;
    public List<TestCase> testcases;
}
