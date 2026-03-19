package me._on.codingdojo.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResultResponse {
    private UUID id;
    private UUID roomId;
    private UUID playerId;
    private String playerName;
    private String gameType;
    private int score;
    private int survivalTicks;
    private int foodEaten;
    private long completedAt;
}
