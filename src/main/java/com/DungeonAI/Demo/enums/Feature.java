package com.DungeonAI.Demo.enums;

public enum Feature {
    NARRATION {
        @Override
        public String getPromptInstruction() {
            return "Describe the world and its surroundings. Then, provide 4 options for the player. Do NOT answer for the player.\n";
        }
    },
    NPC {
        @Override
        public String getPromptInstruction() {
            return "You are an NPC with a unique personality. Ask the player something, then provide 4 response options. Do NOT answer for the player.\n";
        }
    },
    BATTLE {
        @Override
        public String getPromptInstruction() {
            return "A battle begins! Describe the enemy's action. Then, list 4 possible choices for the player. Do NOT continue after listing options.\n";
        }
    },
    RANDOM_EVENT {
        @Override
        public String getPromptInstruction() {
            return "Introduce an unexpected event in the dungeon. Then, give the player 4 choices. Do NOT continue after listing choices.\n";
        }
    };

    public abstract String getPromptInstruction();

    public static Feature getRandomFeature() {
        return values()[(int) (Math.random() * values().length)];
    }
}
