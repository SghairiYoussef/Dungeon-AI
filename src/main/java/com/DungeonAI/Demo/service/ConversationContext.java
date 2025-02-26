package com.DungeonAI.Demo.service;

import org.springframework.stereotype.Component;

@Component
public class ConversationContext {

    private final StringBuilder context = new StringBuilder();
    private boolean isFirstPrompt = true;

    public void appendAIResponse(String response) {
        context.append("AI: ").append(response).append("\n");
    }

    public String getContext() {
        return context.toString();
    }

    public boolean isFirstPrompt() {
        return isFirstPrompt;
    }

    public void markPromptUsed() {
        this.isFirstPrompt = false;
    }
}
