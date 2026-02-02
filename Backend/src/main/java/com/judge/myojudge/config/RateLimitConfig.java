package com.judge.myojudge.config;

import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


@Configuration
public class RateLimitConfig {

    @Value("${api.max.request}")
    Long maxRequest;
    @Value("${api.max.request.time-duration}")
    Long limitTime;
    @Bean
    public Bucket createBucket(){
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(maxRequest)
                        .refillIntervally(maxRequest, Duration.ofMillis(limitTime))
                )
                .build();
    }

}
