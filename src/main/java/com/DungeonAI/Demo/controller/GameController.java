package com.DungeonAI.Demo.controller;


import com.DungeonAI.Demo.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/continue")
    public ResponseEntity<String> continueGame(@RequestBody Map<String, String> request) {
        String userInput = request.get("userInput");
        String aiResponse = gameService.getAIResponse(userInput);
        return ResponseEntity.ok(aiResponse);
    }
}
