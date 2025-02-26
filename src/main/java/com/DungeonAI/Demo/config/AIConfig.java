package com.DungeonAI.Demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Getter
    @Value("${api.key}")
    private String apiKey;

    @Value("${hf.model}")
    private String model;

    @Value("${hf.url}")
    private String baseUrl;

    public String getModelUrl() {
        return baseUrl + model;
    }
}
