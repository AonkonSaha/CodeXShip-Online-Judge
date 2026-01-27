package com.judge.myojudge.service.redis.imp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.judge.myojudge.keys.RedisKey;
import com.judge.myojudge.model.dto.ProblemResponse;
import com.judge.myojudge.model.dto.ProblemSampleTcResponse;
import com.judge.myojudge.model.mapper.ProblemMapper;
import com.judge.myojudge.service.redis.ProblemRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProblemRedisServiceImp implements ProblemRedisService {
    private final RedisTemplate<String,Object> redisTemplate;
    private final ProblemMapper problemMapper;
    private final ObjectMapper objectMapper;
    @Override
    public void saveCacheProblem(ProblemSampleTcResponse problemSampleTcResponse, String email) {
        Map<String,Object> map= new  HashMap<>();
        ProblemResponse problemResponse=problemMapper.toProblemResponse(problemSampleTcResponse);
        map.put("problem_response",problemResponse);
        map.put("sample_input",problemSampleTcResponse.getSampleTestcase());
        map.put("sample_output",problemSampleTcResponse.getSampleOutput());
        map.put("sample_output",problemSampleTcResponse.getSampleOutput());
        map.putIfAbsent("testcase_paths",new HashMap<String,Object>());
        redisTemplate.opsForHash().putAll(RedisKey.PROBLEM_KEY+problemSampleTcResponse.getId(),map);
        if(email != null && !email.isEmpty() && problemSampleTcResponse.isSolved()) {
            redisTemplate.opsForSet()
                    .add(RedisKey.USER_SOLVED_PROBLEMS + email, problemSampleTcResponse.getId());
        }
    }

    @Override
    public ProblemSampleTcResponse findCacheProblem(Long id,String email) {
        Map<Object,Object> cachedData=redisTemplate.opsForHash().entries(RedisKey.PROBLEM_KEY+id);
        if(cachedData.isEmpty()){
            return null;
        }
        ProblemResponse problemResponse=objectMapper.convertValue(cachedData.get("problem_response"),ProblemResponse.class);
        List<String> sampleInput=objectMapper.convertValue(cachedData.get("sample_input"), new TypeReference<List<String>>() {
        });
        List<String> sampleOutput=objectMapper.convertValue(cachedData.get("sample_output"), new TypeReference<List<String>>() {
        });
        ProblemSampleTcResponse problemSampleTcResponse=problemMapper.toProblemSampleTestCaseResponse(problemResponse);
        problemSampleTcResponse.setSampleTestcase(sampleInput);
        problemSampleTcResponse.setSampleOutput(sampleOutput);
        if(email!=null && !email.isEmpty()){
            Boolean isSolved = redisTemplate.opsForSet()
                    .isMember(RedisKey.USER_SOLVED_PROBLEMS + email, id);
            problemSampleTcResponse.setSolved(Boolean.TRUE.equals(isSolved));
        }
        return problemSampleTcResponse;
    }
}
