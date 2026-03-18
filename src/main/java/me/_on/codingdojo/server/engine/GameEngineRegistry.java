package me._on.codingdojo.server.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for game engine implementations.
 * Maps game types to their corresponding engine implementations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GameEngineRegistry {
    private final Map<String, GameEngine> engines = new ConcurrentHashMap<>();

    /**
     * Register a game engine implementation.
     */
    public void register(String gameType, GameEngine engine) {
        engines.put(gameType, engine);
        log.info("Registered game engine for type: {}", gameType);
    }

    /**
     * Get engine by game type.
     *
     * @param gameType the game type identifier
     * @return the registered GameEngine instance
     * @throws GameEngineNotFoundException if no engine is registered for the given type
     */
    public GameEngine getEngine(String gameType) {
        GameEngine engine = engines.get(gameType);
        if (engine == null) {
            throw new GameEngineNotFoundException("No engine registered for type: " + gameType);
        }
        return engine;
    }

    /**
     * Check if engine is registered for the given game type.
     *
     * @param gameType the game type identifier
     * @return true if an engine is registered, false otherwise
     */
    public boolean hasEngine(String gameType) {
        return engines.containsKey(gameType);
    }

    /**
     * List all registered game types.
     *
     * @return a set of all registered game type identifiers
     */
    public Set<String> getRegisteredGameTypes() {
        return Set.copyOf(engines.keySet());
    }
}
