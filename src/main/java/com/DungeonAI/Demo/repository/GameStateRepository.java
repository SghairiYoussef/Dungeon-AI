package com.DungeonAI.Demo.repository;

import com.DungeonAI.Demo.model.GameState;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GameStateRepository extends JpaRepository<GameState, Long> {
    Optional<GameState> findByGuestId(String guestId);
}
