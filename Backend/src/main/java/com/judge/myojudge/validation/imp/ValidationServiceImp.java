package com.judge.myojudge.validation.imp;

import com.judge.myojudge.exception.InvalidProblemArgumentException;
import com.judge.myojudge.exception.InvalidTestCaseArgumentException;
import com.judge.myojudge.model.dto.ProblemDTO;
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
    public void validateProblemDetails(ProblemDTO problemDTO) {
        if(problemValidation.isEmptyProblemTitle(problemDTO.getTitle())){
            throw new InvalidProblemArgumentException("Title is empty");
        }
        if(problemValidation.isEmptyProblemHandle(problemDTO.getHandle())){
            throw new InvalidProblemArgumentException("Handle is empty");
        }
        if(problemValidation.isEmptyProblemType(problemDTO.getType())){
            throw new InvalidProblemArgumentException("Type is empty");
        }
        if(problemValidation.isEmptyProblemDifficulty(problemDTO.getDifficulty())){
            throw new InvalidProblemArgumentException("Difficulty is empty");
        }
        if(problemValidation.isEmptyProblemStatement(problemDTO.getProblemStatement())){
            throw new InvalidProblemArgumentException("Statement is empty");
        }
        if(problemValidation.isEmptyProblemTestcases(problemDTO.getFiles())){
            throw new InvalidProblemArgumentException("Testcases is empty");
        }
        if(problemValidation.isExitProblemHandle(problemDTO.getHandle())){
            throw new InvalidProblemArgumentException("Handle is already exit");
        }
        if(problemValidation.isExitProblemTitle(problemDTO.getTitle())){
            throw new InvalidProblemArgumentException("Title is already exit");
        }
        if(problemValidation.isOverProblemStatementLimit(problemDTO.getProblemStatement())){
            throw new InvalidProblemArgumentException("Statement must be contained 8000 characters");
        }
        if(problemValidation.isOverProblemTestcaseLimit(problemDTO.getFiles())){
            throw new InvalidProblemArgumentException("Testcase File must be contained 5MB");
        }

        if(!problemValidation.isMissMatchTestcase(problemDTO.getFiles()).isEmpty()){
            List<String> missMatch=problemValidation.isMissMatchTestcase(problemDTO.getFiles());
            throw new InvalidTestCaseArgumentException("Testcase MissMatch Number :  "+missMatch);
        }
    }
}
