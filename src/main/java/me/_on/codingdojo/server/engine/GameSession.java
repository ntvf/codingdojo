package me._on.codingdojo.server.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me._on.codingdojo.server.model.GameState;
import me._on.codingdojo.server.model.Move;
import me._on.codingdojo.server.model.Round;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSession {
    private UUID roomId;
    private GameEngine gameEngine;
    private GameState currentState;
    private final ConcurrentHashMap<UUID, Move> pendingMoves = new ConcurrentHashMap<>();
    private Round currentRound;
    private long createdAt;

    public void submitMove(UUID playerId, Move move) {
        pendingMoves.put(playerId, move);
    }

    public Map<UUID, Move> getAndClearPendingMoves() {
        Map<UUID, Move> moves = new HashMap<>(pendingMoves);
        pendingMoves.clear();
        return moves;
    }

    public void updateState(GameState newState) {
        this.currentState = newState;
    }

    public boolean isRoundComplete() {
        return gameEngine.isRoundOver(currentState);
    }
}
