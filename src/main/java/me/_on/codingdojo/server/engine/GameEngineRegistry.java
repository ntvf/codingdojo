package me._on.codingdojo.server.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameEngineRegistry {
    private final Map<String, GameEngine> engines = new ConcurrentHashMap<>();

    public void register(String gameType, GameEngine engine) {
        engines.put(gameType, engine);
        log.info("Registered game engine for type: {}", gameType);
    }

    public GameEngine getEngine(String gameType) {
        GameEngine engine = engines.get(gameType);
        if (engine == null) {
            throw new GameEngineNotFoundException("No engine registered for type: " + gameType);
        }
        return engine;
    }

    public boolean hasEngine(String gameType) {
        return engines.containsKey(gameType);
    }

    public Set<String> getRegisteredGameTypes() {
        return Set.copyOf(engines.keySet());
    }
}
