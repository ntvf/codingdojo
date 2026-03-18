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

/**
 * Represents an active game session.
 *
 * Holds the state, moves, and configuration for a single game session in a room.
 * Thread-safe for concurrent move submission from multiple players.
 */
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

    /**
     * Submit a move from a player (thread-safe).
     *
     * @param playerId the UUID of the player submitting the move
     * @param move the move to submit
     */
    public void submitMove(UUID playerId, Move move) {
        pendingMoves.put(playerId, move);
    }

    /**
     * Get all pending moves and clear them.
     *
     * Creates a snapshot of current moves, clears the collection, and returns the snapshot.
     * This is used after each game tick to process all accumulated moves.
     *
     * @return a map of player IDs to their moves
     */
    public Map<UUID, Move> getAndClearPendingMoves() {
        Map<UUID, Move> moves = new HashMap<>(pendingMoves);
        pendingMoves.clear();
        return moves;
    }

    /**
     * Update game state after a tick.
     *
     * @param newState the new game state
     */
    public void updateState(GameState newState) {
        this.currentState = newState;
    }

    /**
     * Check if round is complete.
     *
     * @return true if the current round is over, false otherwise
     */
    public boolean isRoundComplete() {
        return gameEngine.isRoundOver(currentState);
    }
}
