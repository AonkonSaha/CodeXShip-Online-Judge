package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.ProblemDTO;
import com.judge.myojudge.model.dto.ProblemWithSample;
import com.judge.myojudge.model.entity.Problem;

public interface ProblemMapper {
    ProblemWithSample toProblemWithSample(Problem problem);

    ProblemDTO toProblemDTO(Problem problem);

    Problem toProblem(String title, String handle, String difficulty, String type, Long coin, double timeLimit, double memoryLimit, String problemStatement, String explanation);
}
