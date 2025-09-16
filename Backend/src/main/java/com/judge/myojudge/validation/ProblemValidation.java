package com.judge.myojudge.validation;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public interface ProblemValidation {

    boolean isEmptyProblemTitle(String title);
    boolean isEmptyProblemHandle(String handle);
    boolean isEmptyProblemType(String type);
    boolean isEmptyProblemDifficulty(String difficulty);
    boolean isEmptyProblemStatement(String problemStatement);
    boolean isEmptyProblemTestcases(List<MultipartFile> testcases);
    boolean isExitProblemHandle(String handle);
    boolean isExitProblemTitle(String title);
    boolean isOverProblemStatementLimit(String problemStatement);
    boolean isOverProblemTestcaseLimit(List<MultipartFile> testcases);
    List<String> isMissMatchTestcase(List<MultipartFile> testcases);

}
