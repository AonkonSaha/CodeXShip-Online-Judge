package com.judge.myojudge.model.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
    private Map<String,String> testCaseNameWithPath;
    private List<MultipartFile> files;
    private Long coins;
    private String explanation;

    public ProblemDTO(String title, String handle, String difficulty, String type, String problemStatement, String explanation, List<MultipartFile> files) {
        this.title = title;
        this.handle = handle;
        this.difficulty = difficulty;
        this.type = type;
        this.problemStatement = problemStatement;
        this.explanation = explanation;
        this.files = files ;
    }

    public ProblemDTO(String title, String handle, String difficulty, String type, String problemStatement) {
        this.title = title;
        this.handle = handle;
        this.difficulty = difficulty;
        this.type = type;
        this.problemStatement = problemStatement;
    }

}
