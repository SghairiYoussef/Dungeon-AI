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

    private final StringBuilder conversationContext = new StringBuilder();
    private boolean isFirstPrompt = true;
    private Feature feature = Feature.NARRATION;

    public String getAIResponse(String userInput) {
        try {
            if (isFirstPrompt) {
                conversationContext.append("You are a Dungeon master, in this scenario I am the player and you're dictating the game. The story starts as follows: ").append(userInput);
                isFirstPrompt = false;
            }
            else{
                conversationContext.append(userInput);
            }

            switch (feature) {
                case NARRATION:
                    conversationContext.append("\nDescribe the world and its surroundings and give the player a choice");
                    break;
                case NPC:
                    conversationContext.append("\nYou are now a unique NPC with a distinct personality. You ask the player something. and wait for a response");
                    break;
                case BATTLE:
                    conversationContext.append("\nYou prepare to attack the player as an NPC, what move do you do? and let the player respond");
                    break;
                case RANDOM_EVENT:
                    conversationContext.append("\nGive a random event");
                    break;
            }

            String letAnswerPrompt = "STOP when the next decision must be made. Do not answer for the user.";
            conversationContext.append(letAnswerPrompt);
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
                    generatedText = generatedText.replace(conversationContext.toString(), "");
                }
            } else if (rawResponse instanceof Map) {
                Map<String, Object> mapResponse = (Map<String, Object>) rawResponse;
                if (mapResponse.containsKey("generated_text")) {
                    generatedText = mapResponse.get("generated_text").toString();
                }
            }

            conversationContext.append("this is the context")
                    .append(generatedText)
                    .append("\n");

            conversationContext.replace(0,
                    conversationContext.length(),
                    conversationContext.toString()
                            .replace(letAnswerPrompt, ""));

            feature = Feature.getRandomFeature();
            return generatedText;
        } catch (Exception e) {
            return "Error processing AI response: " + e.getMessage();
        }
    }
}
