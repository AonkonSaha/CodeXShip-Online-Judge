package com.judge.myojudge.model.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionResponse {
    private Long id;
    private String problemName;
    private int total;
    private int passed;
    private String verdict;
    private float time;
    private Long memory;
    private Long coins=0L;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    private List<TestcaseResultDTO> results;
    private boolean completed=false;

}
