package com.judge.myojudge.service.redis;

import com.judge.myojudge.model.dto.redis.CacheProblem;
import com.judge.myojudge.model.dto.redis.CacheSampleProblem;

import java.util.Set;

public interface ProblemRedisService {
    void saveCacheProblem(CacheSampleProblem cacheSampleProblem);

    CacheSampleProblem findCacheProblem(Long id);

    void saveCacheSolvedProblem(Long id, String email);

    void saveCachePaginationAndFilter(
            CacheProblem cacheProblem,
            String category, int page, int size,
            String search, String difficulty, String solvedFilter
    );
    Set<Long> findCacheSolvedProblems(String email);

    CacheProblem findCachePaginationAndFilter(
            String category,
            int page, int size,
            String search,
            String difficulty,
            String solvedFilter
    );

    Boolean findCacheProblemIsSolved(Long id, String email);

    void saveCacheSolvedProblems(Set<Long> solvedIds, String email);
}