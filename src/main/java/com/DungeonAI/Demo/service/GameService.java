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

enum Feature {
    NARRATION,
    NPC,
    BATTLE,
    RANDOM_EVENT;

    public static Feature getRandomFeature() {
        return values()[(int) (Math.random() * values().length)];
    }
}

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
    private Feature feature = Feature.NARRATION;

    public String getAIResponse(String userInput) {
        try {
            StringBuilder promptBuilder = new StringBuilder();

            if (isFirstPrompt) {
                promptBuilder.append("You are a Dungeon Master. I am the player, and you dictate the game. The story begins as follows: ")
                        .append(userInput).append("\n");
                isFirstPrompt = false;
            } else {
                promptBuilder.append("Player: ").append(userInput).append("\n");
            }

            // Append the feature-specific instruction
            switch (feature) {
                case NARRATION:
                    promptBuilder.append("Describe the world and its surroundings. Then, provide 4 options for the player. Do NOT answer for the player.\n");
                    break;
                case NPC:
                    promptBuilder.append("You are an NPC with a unique personality. Ask the player something, then provide 4 response options. Do NOT answer for the player.\n");
                    break;
                case BATTLE:
                    promptBuilder.append("A battle begins! Describe the enemy's action. Then, list 4 possible choices for the player. Do NOT continue after listing options.\n");
                    break;
                case RANDOM_EVENT:
                    promptBuilder.append("Introduce an unexpected event in the dungeon. Then, give the player 4 choices. Do NOT continue after listing choices.\n");
                    break;
            }

            // Append stop condition
            promptBuilder.append("Ensure that you only provide a description followed by choices. Do NOT answer for the player.");

            String prompt = promptBuilder.toString();

            Map<String, String> requestBody = Map.of("inputs", prompt);
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // Create request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HF_URL))
                    .header("Authorization", "Bearer " + HF_API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse response
            Object rawResponse = objectMapper.readValue(response.body(), Object.class);
            String generatedText = "No response from AI.";

            if (rawResponse instanceof List<?>) {
                List<Map<String, Object>> listResponse = (List<Map<String, Object>>) rawResponse;
                if (!listResponse.isEmpty()) {
                    generatedText = listResponse.get(0).get("generated_text").toString();
                    generatedText = generatedText.replace(promptBuilder.toString(), "");
                }
            } else if (rawResponse instanceof Map) {
                Map<String, Object> mapResponse = (Map<String, Object>) rawResponse;
                if (mapResponse.containsKey("generated_text")) {
                    generatedText = mapResponse.get("generated_text").toString();
                    generatedText = generatedText.replace(promptBuilder.toString(), "");
                }
            }

            // Store only the relevant game progress (avoiding AI responses causing repetition)
            conversationContext.append("AI: ").append(generatedText).append("\n");

            // Randomly select the next feature
            feature = Feature.getRandomFeature();

            System.out.println("Next feature: " + feature);
            System.out.println("Updated conversation context: \n" + conversationContext);

            return generatedText;
        } catch (Exception e) {
            return "Error processing AI response: " + e.getMessage();
        }
    }
}
