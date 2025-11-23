package com.judge.myojudge.model.mapper.imp;

import com.judge.myojudge.model.dto.ProblemDTO;
import com.judge.myojudge.model.dto.ProblemWithSample;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.mapper.ProblemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProblemMapperImp implements ProblemMapper {
    @Override
    public ProblemWithSample toProblemWithSample(Problem problem) {
        return ProblemWithSample.builder()
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
    public ProblemDTO toProblemDTO(Problem problem) {
        ProblemDTO problemDTO = new ProblemDTO();
        problemDTO.setTitle(problem.getTitle());
        problemDTO.setDifficulty(problem.getDifficulty());
        problemDTO.setType(problem.getType());
        problemDTO.setHandle(problem.getHandleName());
        problemDTO.setCoins(problem.getCoins());
        problemDTO.setProblemStatement(problem.getProblemStatement());
        problemDTO.setExplanation(problem.getExplanation());
        problemDTO.setMemoryLimit(problem.getMemoryLimit() == null ? 0 : problem.getMemoryLimit());
        problemDTO.setTimeLimit(problem.getTimeLimit() == null ? 0 : problem.getTimeLimit());
        return problemDTO;
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
