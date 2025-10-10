package com.judge.myojudge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Judge0Config {

    @Value("${judge0.url}")
    private String url;

    @Value("${judge0.apiKey}")
    private String apiKey;

    public String getUrl() { return url; }
    public String getApiKey() { return apiKey; }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(url) // Base URL for Judge0 API
                .defaultHeader("X-RapidAPI-Key", apiKey)
                .defaultHeader("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

}
