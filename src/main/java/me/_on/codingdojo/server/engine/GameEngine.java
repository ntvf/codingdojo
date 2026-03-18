package me._on.codingdojo.server.engine;

import me._on.codingdojo.server.model.GameState;
import me._on.codingdojo.server.model.Move;
import me._on.codingdojo.server.model.Room;
import me._on.codingdojo.server.engine.map.MapConfig;

import java.util.Map;
import java.util.UUID;

/**
 * Contract for game engine implementations.
 * Each game type (Snake, etc.) implements this interface to define
 * how game state is initialized, updated, and checked for completion.
 */
public interface GameEngine {
    /**
     * Initialize the game state for a new room.
     * @param room the room configuration
     * @param mapConfig the map configuration
     * @return initialized GameState
     */
    GameState initState(Room room, MapConfig mapConfig);

    /**
     * Execute one game tick with player moves.
     * @param currentState the current game state
     * @param playerMoves map of player UUID to Move
     * @return new game state after tick
     */
    GameState tick(GameState currentState, Map<UUID, Move> playerMoves);

    /**
     * Check if the current round is over.
     * @param gameState the current game state
     * @return true if round should end
     */
    boolean isRoundOver(GameState gameState);
}
