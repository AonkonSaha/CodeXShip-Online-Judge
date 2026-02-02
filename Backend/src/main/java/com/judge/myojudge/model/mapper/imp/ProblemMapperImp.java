package com.judge.myojudge.model.mapper.imp;

import com.judge.myojudge.model.dto.ProblemResponse;
import com.judge.myojudge.model.dto.ProblemSampleTcResponse;
import com.judge.myojudge.model.dto.ProblemTcFileResponse;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.TestCase;
import com.judge.myojudge.model.mapper.ProblemMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProblemMapperImp implements ProblemMapper {
    @Override
    public ProblemSampleTcResponse toProblemSampleTestCaseResponse(Problem problem) {
        return ProblemSampleTcResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .problemStatement(problem.getProblemStatement())
                .explanation(problem.getExplanation())
                .difficulty(problem.getDifficulty())
                .type(problem.getType())
                .handle(problem.getHandleName())
                .coins(problem.getCoins())
                .timeLimit(problem.getTimeLimit()==null?0:problem.getTimeLimit())
                .memoryLimit(problem.getMemoryLimit()==null?0:problem.getMemoryLimit())
                .build();
    }

    @Override
    public ProblemSampleTcResponse toProblemSampleTestCaseResponse(ProblemResponse problem) {
        return ProblemSampleTcResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .difficulty(problem.getDifficulty())
                .type(problem.getType())
                .coins(problem.getCoins())
                .build();
    }

    @Override
    @Transactional(value = Transactional.TxType.SUPPORTS)
    public ProblemResponse toProblemResponse(Problem problem) {
        ProblemResponse problemResponse = new ProblemResponse();
        problemResponse.setId(problem.getId());
        problemResponse.setTitle(problem.getTitle());
        problemResponse.setDifficulty(problem.getDifficulty());
        problemResponse.setType(problem.getType());
        problemResponse.setCoins(problem.getCoins());
        return problemResponse;
    }

    @Override
    public ProblemResponse toProblemResponse(ProblemSampleTcResponse problem) {
        return ProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .difficulty(problem.getDifficulty())
                .type(problem.getType())
                .coins(problem.getCoins())
                .build();
    }

    @Override
    @Transactional(value = Transactional.TxType.SUPPORTS)
    public ProblemTcFileResponse toProblemTcFileResponse(Problem problem) {
        ProblemTcFileResponse problemResponse = new ProblemTcFileResponse();
        problemResponse.setId(problem.getId());
        problemResponse.setTitle(problem.getTitle());
        problemResponse.setDifficulty(problem.getDifficulty());
        problemResponse.setType(problem.getType());
        problemResponse.setHandle(problem.getHandleName());
        problemResponse.setCoins(problem.getCoins());
        problemResponse.setProblemStatement(problem.getProblemStatement());
        problemResponse.setExplanation(problem.getExplanation());
        problemResponse.setMemoryLimit(problem.getMemoryLimit() == null ? 0 : problem.getMemoryLimit());
        problemResponse.setTimeLimit(problem.getTimeLimit() == null ? 0 : problem.getTimeLimit());
        for(TestCase testCase:problem.getTestcases()){
            problemResponse.getTestCaseNameWithPath().put(testCase.getFileName(),testCase.getFilePath());
        }
        return problemResponse;
    }





    @Override
    public Problem toProblem(String title, String handle, String difficulty, String type, Long coin, double timeLimit, double memoryLimit, String problemStatement, String explanation) {
        Problem problem = new Problem();
        problem.setTitle(title);
        problem.setHandleName(handle);
        problem.setDifficulty(difficulty);
        problem.setType(type);
        problem.setCoins(coin);
        problem.setTimeLimit(timeLimit);
        problem.setMemoryLimit(memoryLimit);
        problem.setProblemStatement(problemStatement);
        problem.setExplanation(explanation);
        return problem;
    }
}
