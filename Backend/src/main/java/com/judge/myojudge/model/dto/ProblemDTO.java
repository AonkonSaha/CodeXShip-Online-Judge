package com.judge.myojudge.model.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.judge.myojudge.model.entity.TestCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProblemDTO {
    private String title;
    private String handle;
    private String difficulty;
    private String type;
    @JsonProperty("problem_statement")
    private String problemStatement;
    private List<MultipartFile> testcasesWithFile;
    private List<TestCase> testcases;

    public ProblemDTO(String title, String handle, String difficulty, String type, String problemStatement, List<MultipartFile> testcasesWithFile) {
        this.title = title;
        this.handle = handle;
        this.difficulty = difficulty;
        this.type = type;
        this.problemStatement = problemStatement;
        this.testcasesWithFile = testcasesWithFile;
    }
}
