package com.DungeonAI.Demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Service
public class GameService {

    @Value("${api.key}")
    private String HF_API_KEY;
    private final String HF_MODEL = "mistralai/Mistral-7B-Instruct-v0.3";
    private final String HF_URL = "https://api-inference.huggingface.co/models/" + HF_MODEL;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private StringBuilder conversationContext = new StringBuilder();
    private boolean isFirstPrompt = true;

    public String getAIResponse(String userInput) {
        try {
            if (isFirstPrompt) {
                conversationContext.append("We're writing a story together. I start with: ").append(userInput).append(" what happens next?\n");
                isFirstPrompt = false;
            }
            else{
                conversationContext.append(userInput);
            }
            String prompt = conversationContext.toString();

            Map<String, String> requestBody = Map.of("inputs", prompt);
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // Create the request to Hugging Face API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HF_URL))
                    .header("Authorization", "Bearer " + HF_API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the response
            Object rawResponse = objectMapper.readValue(response.body(), Object.class);

            String generatedText = "No response from AI.";

            if (rawResponse instanceof List<?>) {
                List<Map<String, Object>> listResponse = (List<Map<String, Object>>) rawResponse;
                if (!listResponse.isEmpty()) {
                    generatedText = listResponse.get(0).get("generated_text").toString();
                }
            } else if (rawResponse instanceof Map) {
                Map<String, Object> mapResponse = (Map<String, Object>) rawResponse;
                if (mapResponse.containsKey("generated_text")) {
                    generatedText = mapResponse.get("generated_text").toString();
                }
            }
            conversationContext.append(generatedText).append("\n");

            return generatedText;
        } catch (Exception e) {
            return "Error processing AI response: " + e.getMessage();
        }
    }
}
