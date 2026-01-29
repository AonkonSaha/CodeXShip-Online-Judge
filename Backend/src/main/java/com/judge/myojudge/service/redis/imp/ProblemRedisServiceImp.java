package com.judge.myojudge.service.redis.imp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.judge.myojudge.keys.RedisKey;
import com.judge.myojudge.model.dto.redis.CacheProblem;
import com.judge.myojudge.model.dto.redis.CacheSampleProblem;
import com.judge.myojudge.model.mapper.ProblemMapper;
import com.judge.myojudge.service.ProblemService;
import com.judge.myojudge.service.redis.ProblemRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProblemRedisServiceImp implements ProblemRedisService {
    private final RedisTemplate<String,Object> redisTemplate;
    private final ProblemMapper problemMapper;
    private final ObjectMapper objectMapper;
    private final ProblemService problemService;
    @Override
    public void saveCacheProblem(CacheSampleProblem cacheSampleProblem) {
        redisTemplate.opsForValue().set(RedisKey.PROBLEM_KEY+cacheSampleProblem.getProblemSampleTcResponse().getId(),cacheSampleProblem);
    }

    @Override
    public CacheSampleProblem findCacheProblem(Long id) {
        return objectMapper.convertValue(redisTemplate.opsForValue().get(RedisKey.PROBLEM_KEY+id),CacheSampleProblem.class);
    }

    @Override
    public void saveCacheSolvedProblem(Long id, String email) {
        redisTemplate.opsForSet().add(RedisKey.USER_SOLVED_PROBLEMS+email,id);
    }

    @Override
    public void saveCachePaginationAndFilter(
            CacheProblem cacheProblem,
            String category,
            int page, int size,
            String search,
            String difficulty,
            String solvedFilter
    ) {
        redisTemplate.opsForValue().set(RedisKey.PROBLEM_PAGINATION_KEY+
                category+
                page+
                size+
                search+
                difficulty+
                solvedFilter,cacheProblem);
    }


    @Override
    public Set<Long> findCacheSolvedProblems(String email) {
        return objectMapper.convertValue(redisTemplate.opsForSet().members(RedisKey.USER_SOLVED_PROBLEMS + email), new TypeReference<Set<Long>>() {
        });
    }

    @Override
    public CacheProblem findCachePaginationAndFilter(
            String category,
            int page,
            int size,
            String search,
            String difficulty,
            String solvedFilter
    ) {
        return objectMapper.convertValue(redisTemplate.opsForValue().get(RedisKey.PROBLEM_PAGINATION_KEY+
                category+
                page+
                size+
                search+
                difficulty+
                solvedFilter),CacheProblem.class);
    }


    @Override
    public Boolean findCacheProblemIsSolved(Long id, String email) {
        return objectMapper.convertValue(redisTemplate.opsForSet().isMember(RedisKey.USER_SOLVED_PROBLEMS+email,id), Boolean.class);
    }

    @Override
    public void saveCacheSolvedProblems(Set<Long> solvedIds, String email) {
         redisTemplate.opsForSet().add(RedisKey.USER_SOLVED_PROBLEMS+email,solvedIds.toArray(new Long[0]));
    }
}