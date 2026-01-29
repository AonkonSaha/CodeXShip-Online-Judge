package com.judge.myojudge.model.dto.redis;

import com.judge.myojudge.model.dto.ProblemSampleTcResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CacheSampleProblem {
    private ProblemSampleTcResponse problemSampleTcResponse;
    private Set<String> userEmails=new HashSet<>();
}
