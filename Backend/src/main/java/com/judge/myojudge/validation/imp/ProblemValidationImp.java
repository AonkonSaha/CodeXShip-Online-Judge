package com.judge.myojudge.validation.imp;

import com.judge.myojudge.exception.InvalidTestCaseArgumentException;
import com.judge.myojudge.repository.ProblemRepo;
import com.judge.myojudge.validation.ProblemValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ProblemValidationImp implements ProblemValidation {

    private final ProblemRepo problemRepo;

    @Override
    public boolean isEmptyProblemTitle(String title) {
        return title==null || title.isEmpty();
    }

    @Override
    public boolean isEmptyProblemHandle(String handle) {
        return handle==null || handle.isEmpty();
    }

    @Override
    public boolean isEmptyProblemType(String type) {
        return type==null || type.isEmpty();
    }

    @Override
    public boolean isEmptyProblemDifficulty(String difficulty) {
        return difficulty==null || difficulty.isEmpty();
    }

    @Override
    public boolean isEmptyProblemStatement(String problemStatement) {
        return problemStatement==null || problemStatement.isEmpty();
    }

    @Override
    public boolean isEmptyProblemTestcases(List<MultipartFile> testcases) {
        return testcases==null || testcases.isEmpty();
    }

    @Override
    public boolean isExitProblemHandle(String handle) {
        return problemRepo.existsByHandleName(handle);
    }

    @Override
    public boolean isExitProblemTitle(String title) {
        return problemRepo.existsByTitle(title);
    }

    @Override
    public boolean isOverProblemStatementLimit(String problemStatement) {
        return problemStatement.length()>3000;
    }

    @Override
    public boolean isOverProblemTestcaseLimit(List<MultipartFile> testcases) {
       return false;
    }

    @Override
    public List<String> isMissMatchTestcase(List<MultipartFile> testcases) {
        Set<String> inputCases=new TreeSet<>();
        Set<String> outputCases=new TreeSet<>();
        for (MultipartFile testcase : testcases) {
            String testcaseName = testcase.getOriginalFilename();
            if(testcaseName==null || testcaseName.isEmpty()){
                throw new InvalidTestCaseArgumentException("Testcase Name is Empty");
            }
            if(testcaseName.endsWith(".in")){
                String caseNumber=testcaseName.replace(".in","");
                inputCases.add(caseNumber);
            }
            else{
                String caseNumber=testcaseName.replace(".out","");
                outputCases.add(caseNumber);
            }
        }
        Iterator<String> inputCasesIterator=inputCases.iterator();
        Iterator<String> outputCasesIterator=outputCases.iterator();

        while(inputCasesIterator.hasNext() && outputCasesIterator.hasNext()){
            String in=inputCasesIterator.next();
            String out=outputCasesIterator.next();
            if(!in.equals(out)){
                return Arrays.asList(in,out);
            }
        }

        return new ArrayList<>();
    }
}
