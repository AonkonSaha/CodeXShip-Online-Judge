package com.judge.myojudge.model.dto.redis;

import com.judge.myojudge.model.dto.ProblemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CacheProblem {
    private ProblemResponse problemResponse;
    private Map<String,String> testCaseNameWithPath = new HashMap<>();
    private List<String> sampleTestcase;
    private List<String> sampleOutput;
}
