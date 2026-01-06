package com.judge.myojudge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("judgeExecutor")
    public ExecutorService judgeExecutor() {
        return Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("JudgeThread-", 0).factory()
        );
    }
}
