package com.judge.myojudge.service.redis;

import com.judge.myojudge.model.dto.ProblemSampleTcResponse;

public interface ProblemRedisService {
    void saveCacheProblem(ProblemSampleTcResponse problemSampleTcResponse, String mobileOrEmail);

    ProblemSampleTcResponse findCacheProblem(Long id,String email);
}
