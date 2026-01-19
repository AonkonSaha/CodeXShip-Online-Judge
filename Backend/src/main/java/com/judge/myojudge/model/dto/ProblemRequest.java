package com.judge.myojudge.model.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ProblemRequest {
    private String title;
    private String handle;
    private String difficulty;
    private String type;
    @JsonProperty("problem_statement")
    private String problemStatement;
    private List<MultipartFile> files;
    private Long coins;
    private String explanation;
    @JsonProperty("time_limit")
    private double timeLimit;
    @JsonProperty("memory_limit")
    private double memoryLimit;
    public ProblemRequest(String title, String handle, String difficulty, String type, String problemStatement, String explanation, List<MultipartFile> files) {
        this.title = title;
        this.handle = handle;
        this.difficulty = difficulty;
        this.type = type;
        this.problemStatement = problemStatement;
        this.explanation = explanation;
        this.files = files ;
    }

    public ProblemRequest(String title, String handle, String difficulty, String type, String problemStatement) {
        this.title = title;
        this.handle = handle;
        this.difficulty = difficulty;
        this.type = type;
        this.problemStatement = problemStatement;
    }

}
