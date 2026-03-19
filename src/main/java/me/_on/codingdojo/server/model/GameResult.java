package me._on.codingdojo.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "game_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID roomId;

    @Column(nullable = false)
    private UUID playerId;

    @Column(nullable = false)
    private String playerName;

    @Column(nullable = false)
    private String gameType;

    @Column(nullable = false)
    private int score;

    @Column
    private int survivalTicks;

    @Column
    private String finalDirection;

    @Column
    private int foodEaten;

    @Column(nullable = false)
    private long completedAt;

    @Column(nullable = false, updatable = false)
    private long createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
        if (completedAt == 0) {
            completedAt = System.currentTimeMillis();
        }
    }
}
