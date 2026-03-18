package me._on.codingdojo.server.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me._on.codingdojo.server.engine.map.MapConfig;
import me._on.codingdojo.server.model.GameState;
import me._on.codingdojo.server.model.Move;
import me._on.codingdojo.server.model.Player;
import me._on.codingdojo.server.model.Room;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Game manager service for managing multiple active game sessions.
 *
 * Coordinates between GameEngine, GameLoop, and GameSession to manage
 * the lifecycle of active games. One session per room.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GameManager {
    private final GameEngineRegistry engineRegistry;
    private final GameLoop gameLoop;
    private final Map<UUID, GameSession> activeSessions = new ConcurrentHashMap<>();

    /**
     * Create and start a new game session for a room.
     *
     * @param roomId the UUID of the room
     * @param gameType the type of game (e.g., "snake")
     * @param mapConfig the map configuration
     * @param players the list of players
     * @param room the room entity
     * @return the created GameSession
     */
    public GameSession startGame(UUID roomId, String gameType, MapConfig mapConfig, List<Player> players, Room room) {
        GameEngine engine = engineRegistry.getEngine(gameType);
        GameState initialState = engine.initState(room, mapConfig);

        GameSession session = new GameSession();
        session.setRoomId(roomId);
        session.setGameEngine(engine);
        session.setCurrentState(initialState);
        session.setCreatedAt(System.currentTimeMillis());

        activeSessions.put(roomId, session);
        gameLoop.startGameLoop(roomId, session);

        log.info("Started game session for room {} with type {}", roomId, gameType);
        return session;
    }

    /**
     * Get active session for a room.
     *
     * @param roomId the UUID of the room
     * @return an Optional containing the GameSession if it exists
     */
    public Optional<GameSession> getSession(UUID roomId) {
        return Optional.ofNullable(activeSessions.get(roomId));
    }

    /**
     * Submit a move from a player.
     *
     * @param roomId the UUID of the room
     * @param playerId the UUID of the player
     * @param move the move to submit
     */
    public void submitMove(UUID roomId, UUID playerId, Move move) {
        activeSessions.get(roomId).submitMove(playerId, move);
    }

    /**
     * End a game session.
     *
     * @param roomId the UUID of the room
     */
    public void endGame(UUID roomId) {
        gameLoop.stopGameLoop(roomId);
        activeSessions.remove(roomId);
        log.info("Ended game session for room {}", roomId);
    }
}
