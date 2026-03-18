package me._on.codingdojo.server.engine;

import me._on.codingdojo.server.model.GameState;
import me._on.codingdojo.server.model.Move;
import me._on.codingdojo.server.model.Room;
import me._on.codingdojo.server.engine.map.MapConfig;

import java.util.Map;
import java.util.UUID;

public interface GameEngine {
    GameState initState(Room room, MapConfig mapConfig);

    GameState tick(GameState currentState, Map<UUID, Move> playerMoves);

    boolean isRoundOver(GameState gameState);
}
