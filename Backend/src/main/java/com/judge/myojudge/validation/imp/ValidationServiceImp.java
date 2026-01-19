package com.judge.myojudge.validation.imp;

import com.judge.myojudge.exception.InvalidProblemArgumentException;
import com.judge.myojudge.exception.InvalidTestCaseArgumentException;
import com.judge.myojudge.model.dto.ProblemRequest;
import com.judge.myojudge.model.mapper.DtoMapper;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.validation.ProblemValidation;
import com.judge.myojudge.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidationServiceImp implements ValidationService {

    private final DtoMapper dtoMapper;
    private final UserRepo userRepo;
    private final ProblemValidation problemValidation;

    @Override
    public void validateProblemDetails(ProblemRequest problemRequest) {
        if(problemValidation.isEmptyProblemTitle(problemRequest.getTitle())){
            throw new InvalidProblemArgumentException("Title is empty");
        }
        if(problemValidation.isEmptyProblemHandle(problemRequest.getHandle())){
            throw new InvalidProblemArgumentException("Handle is empty");
        }
        if(problemValidation.isEmptyProblemType(problemRequest.getType())){
            throw new InvalidProblemArgumentException("Type is empty");
        }
        if(problemValidation.isEmptyProblemDifficulty(problemRequest.getDifficulty())){
            throw new InvalidProblemArgumentException("Difficulty is empty");
        }
        if(problemValidation.isEmptyProblemStatement(problemRequest.getProblemStatement())){
            throw new InvalidProblemArgumentException("Statement is empty");
        }
        if(problemValidation.isEmptyProblemTestcases(problemRequest.getFiles())){
            throw new InvalidProblemArgumentException("Testcases is empty");
        }
        if(problemValidation.isExitProblemHandle(problemRequest.getHandle())){
            throw new InvalidProblemArgumentException("Handle is already exit");
        }
        if(problemValidation.isExitProblemTitle(problemRequest.getTitle())){
            throw new InvalidProblemArgumentException("Title is already exit");
        }
        if(problemValidation.isOverProblemStatementLimit(problemRequest.getProblemStatement())){
            throw new InvalidProblemArgumentException("Statement must be contained 8000 characters");
        }
        if(problemValidation.isOverProblemTestcaseLimit(problemRequest.getFiles())){
            throw new InvalidProblemArgumentException("Testcase File must be contained 5MB");
        }

        if(!problemValidation.isMissMatchTestcase(problemRequest.getFiles()).isEmpty()){
            List<String> missMatch=problemValidation.isMissMatchTestcase(problemRequest.getFiles());
            throw new InvalidTestCaseArgumentException("Testcase MissMatch Number :  "+missMatch);
        }
    }
}
