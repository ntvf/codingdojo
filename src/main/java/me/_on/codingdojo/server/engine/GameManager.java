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

@Service
@RequiredArgsConstructor
@Slf4j
public class GameManager {
    private final GameEngineRegistry engineRegistry;
    private final GameLoop gameLoop;
    private final Map<UUID, GameSession> activeSessions = new ConcurrentHashMap<>();

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

    public Optional<GameSession> getSession(UUID roomId) {
        return Optional.ofNullable(activeSessions.get(roomId));
    }

    public void submitMove(UUID roomId, UUID playerId, Move move) {
        activeSessions.get(roomId).submitMove(playerId, move);
    }

    public void endGame(UUID roomId) {
        gameLoop.stopGameLoop(roomId);
        activeSessions.remove(roomId);
        log.info("Ended game session for room {}", roomId);
    }
}
