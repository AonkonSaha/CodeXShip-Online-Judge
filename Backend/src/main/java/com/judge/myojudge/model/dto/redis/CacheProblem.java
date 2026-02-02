package com.judge.myojudge.model.dto.redis;

import com.judge.myojudge.model.dto.ProblemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CacheProblem {
    private List<ProblemResponse> problemResponses=new ArrayList<>();
    private Long totalElements;
    private Set<String> userEmails=new HashSet<>();//who user this cache hit
}
