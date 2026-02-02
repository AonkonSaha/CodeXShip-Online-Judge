package com.judge.myojudge.model.mapper.imp;

import com.judge.myojudge.model.dto.TestCaseResponse;
import com.judge.myojudge.model.entity.TestCaseResult;
import com.judge.myojudge.model.mapper.TestCaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestCaseMapperImp implements TestCaseMapper {
    @Override
    public TestCaseResponse toTestCaseResponse(TestCaseResult testCaseResult) {
        return TestCaseResponse.builder()
                .memory(testCaseResult.memory)
                .time(testCaseResult.time)
                .passed(testCaseResult.isPassed())
                .status(testCaseResult.status)
                .compileOutput(testCaseResult.compileOutput)
                .expectedOutput(testCaseResult.expectedOutput)
                .build();
    }

    @Override
    public TestCaseResult toTestCaseResult(TestCaseResponse testCaseResponse) {
        return TestCaseResult.builder()

                .build();
    }
}
