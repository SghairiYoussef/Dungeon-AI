package com.DungeonAI.Demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class GameState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guestId;
    @Column(columnDefinition = "TEXT")
    private String gameData;

    private LocalDateTime lastUpdated;

    public GameState() {}
    public GameState(String guestId, String gameData) {
        this.guestId = guestId;
        this.gameData = gameData;
        this.lastUpdated = LocalDateTime.now();
    }
}
