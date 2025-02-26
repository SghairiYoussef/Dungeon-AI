package com.DungeonAI.Demo.service;

import com.DungeonAI.Demo.config.AIConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class AIService {

    private final AIConfig aiConfig;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AIService(AIConfig aiConfig, ObjectMapper objectMapper) {
        this.aiConfig = aiConfig;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    public String sendRequest(String prompt) throws Exception {
        Map<String, String> requestBody = Map.of("inputs", prompt);
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(aiConfig.getModelUrl()))
                .header("Authorization", "Bearer " + aiConfig.getApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
