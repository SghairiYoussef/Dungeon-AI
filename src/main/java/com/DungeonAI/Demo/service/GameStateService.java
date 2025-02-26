package com.DungeonAI.Demo.service;

import com.DungeonAI.Demo.model.GameState;
import com.DungeonAI.Demo.repository.GameStateRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameStateService {
    private final GameStateRepository gameStateRepository;

    public GameStateService(GameStateRepository gameStateRepository) {
        this.gameStateRepository = gameStateRepository;
    }

    public Optional<GameState> getGameState(String guestId) {
        return gameStateRepository.findByGuestId(guestId);
    }

    public GameState saveGameState(String guestId, String gameData) {
    Optional<GameState> existingGame = gameStateRepository.findByGuestId(guestId);

    GameState gameState = existingGame.orElse(new GameState(guestId, gameData));
    gameState.setGameData(gameData);
    gameState.setLastUpdated(java.time.LocalDateTime.now());
    return gameStateRepository.save(gameState);
    }
}
