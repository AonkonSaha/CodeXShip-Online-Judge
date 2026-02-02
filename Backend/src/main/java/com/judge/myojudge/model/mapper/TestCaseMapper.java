package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.TestCaseResponse;
import com.judge.myojudge.model.entity.TestCaseResult;

public interface TestCaseMapper {
    TestCaseResponse toTestCaseResponse(TestCaseResult testCaseResult);

    TestCaseResult toTestCaseResult(TestCaseResponse testCaseResponse);
}
