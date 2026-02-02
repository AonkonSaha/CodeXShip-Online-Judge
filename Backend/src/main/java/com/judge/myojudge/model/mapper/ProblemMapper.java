package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.ProblemResponse;
import com.judge.myojudge.model.dto.ProblemSampleTcResponse;
import com.judge.myojudge.model.dto.ProblemTcFileResponse;
import com.judge.myojudge.model.entity.Problem;

public interface ProblemMapper {
    ProblemSampleTcResponse toProblemSampleTestCaseResponse(Problem problem);
    ProblemSampleTcResponse toProblemSampleTestCaseResponse(ProblemResponse problem);
    ProblemResponse toProblemResponse(Problem problem);
    ProblemResponse toProblemResponse(ProblemSampleTcResponse problemSampleTcResponse);

    Problem toProblem(String title,
                      String handle,
                      String difficulty,
                      String type, Long coin,
                      double timeLimit,
                      double memoryLimit,
                      String problemStatement,
                      String explanation);

    ProblemTcFileResponse toProblemTcFileResponse(Problem problem);

}
