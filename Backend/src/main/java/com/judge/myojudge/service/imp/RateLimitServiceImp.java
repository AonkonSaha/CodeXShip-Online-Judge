package com.judge.myojudge.service.imp;

import com.judge.myojudge.service.RateLimitService;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RateLimitServiceImp implements RateLimitService {

    @Value("${api.max.request}")
    Long maxRequest;
    @Value("${api.max.request.time-duration}")
    Long limitTime;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, k ->
                Bucket.builder()
                        .addLimit(limit -> limit
                                .capacity(maxRequest)
                                .refillIntervally(maxRequest, Duration.ofMillis(limitTime))
                        )
                        .build()
        );
    }
}
