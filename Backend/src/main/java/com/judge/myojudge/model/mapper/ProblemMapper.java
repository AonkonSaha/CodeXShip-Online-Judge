package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.ProblemResponse;
import com.judge.myojudge.model.dto.ProblemSampleTestCaseResponse;
import com.judge.myojudge.model.entity.Problem;

public interface ProblemMapper {
    ProblemSampleTestCaseResponse toProblemSampleTestCaseResponse(Problem problem);

    ProblemResponse toProblemResponse(Problem problem);

    Problem toProblem(String title, String handle, String difficulty, String type, Long coin, double timeLimit, double memoryLimit, String problemStatement, String explanation);
}
