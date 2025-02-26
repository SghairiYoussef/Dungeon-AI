package com.DungeonAI.Demo.service;

import com.DungeonAI.Demo.enums.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GameService {

    private final AIService aiService;
    private final ConversationContext conversationContext;
    private final ObjectMapper objectMapper;
    private Feature feature = Feature.NARRATION;

    public GameService(AIService aiService, ConversationContext conversationContext, ObjectMapper objectMapper) {
        this.aiService = aiService;
        this.conversationContext = conversationContext;
        this.objectMapper = objectMapper;
    }

    public String getAIResponse(String userInput) {
        try {
            String prompt = buildPrompt(userInput);
            String responseJson = aiService.sendRequest(prompt);
            String generatedText = extractGeneratedText(responseJson, prompt);

            conversationContext.appendAIResponse(generatedText);
            feature = Feature.getRandomFeature();  // Select the next feature randomly

            return generatedText;
        } catch (Exception e) {
            return "Error processing AI response: " + e.getMessage();
        }
    }

    private String buildPrompt(String userInput) {
        StringBuilder promptBuilder = new StringBuilder();

        if (conversationContext.isFirstPrompt()) {
            promptBuilder.append("You are a Dungeon Master. I am the player, and you dictate the game. The story begins as follows: ")
                    .append(userInput).append("\n");
            conversationContext.markPromptUsed();
        } else {
            promptBuilder.append("Player: ").append(userInput).append("\n");
        }

        promptBuilder.append(feature.getPromptInstruction());
        promptBuilder.append("Ensure that you only provide a description followed by choices. Do NOT answer for the player.");
        return promptBuilder.toString();
    }

    private String extractGeneratedText(String responseJson, String prompt) throws Exception {
        Object rawResponse = objectMapper.readValue(responseJson, Object.class);
        String generatedText = "No response from AI.";

        if (rawResponse instanceof List<?>) {
            List<Map<String, Object>> listResponse = (List<Map<String, Object>>) rawResponse;
            if (!listResponse.isEmpty() && listResponse.get(0).containsKey("generated_text")) {
                generatedText = listResponse.get(0).get("generated_text").toString().replace(prompt, "");
            }
        } else if (rawResponse instanceof Map<?, ?>) {
            Map<String, Object> mapResponse = (Map<String, Object>) rawResponse;
            if (mapResponse.containsKey("generated_text")) {
                generatedText = mapResponse.get("generated_text").toString().replace(prompt, "");
            }
        }

        return generatedText;
    }
}
