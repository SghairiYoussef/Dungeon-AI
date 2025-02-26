package com.DungeonAI.Demo.controller;

import com.DungeonAI.Demo.model.GameState;
import com.DungeonAI.Demo.service.GameStateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/game")
public class GameStateController {
    private final GameStateService gameStateService;

    public GameStateController(GameStateService gameStateService) {
        this.gameStateService = gameStateService;
    }

    @PostMapping("/save")
    public GameState saveGame(@RequestBody GameState gameState) {
        return gameStateService.saveGameState(gameState.getGuestId(), gameState.getGameData());
    }

    @GetMapping("/load/{guestId}")
    public Optional<GameState> loadGame(@PathVariable String guestId) {
        return gameStateService.getGameState(guestId);
    }
}
