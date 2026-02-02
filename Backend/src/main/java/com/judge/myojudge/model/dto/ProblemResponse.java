package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProblemResponse {
    private Long id;
    private String title;
    private String difficulty;
    private String type;
    private Long coins;
    @JsonProperty("is_solved")
    private boolean isSolved=false;
}
