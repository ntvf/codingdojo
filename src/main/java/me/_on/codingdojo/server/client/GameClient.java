package me._on.codingdojo.server.client;

import java.util.concurrent.CompletableFuture;

public interface GameClient {

    /**
     * Connect to a room and authenticate as a player.
     *
     * @param roomCode the room code to join
     * @param playerName the player's display name
     * @return CompletableFuture with Player containing auth token and ID
     */
    CompletableFuture<Player> connect(String roomCode, String playerName);

    /**
     * Disconnect from the game and clean up resources.
     */
    void disconnect();

    /**
     * Check if client is currently connected.
     *
     * @return true if connected to a room
     */
    boolean isConnected();

    /**
     * Get the current game state.
     *
     * @return CompletableFuture with current GameState
     */
    CompletableFuture<GameState> getGameState();

    /**
     * Submit a move in the current game.
     *
     * @param move the direction to move (direction string like "UP", "DOWN", "LEFT", "RIGHT")
     * @return CompletableFuture completing when move is accepted
     */
    CompletableFuture<Void> submitMove(String move);

    /**
     * Subscribe to game state updates via callback.
     *
     * @param callback function to call when state updates
     */
    void onGameStateUpdate(GameStateCallback callback);

    /**
     * Subscribe to game lifecycle events.
     *
     * @param callback function to call for lifecycle events
     */
    void onGameEvent(GameEventCallback callback);
}
