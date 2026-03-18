package me._on.codingdojo.server.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.util.Map;
import java.util.UUID;

@Data
@SuperBuilder
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes({
    // Concrete game state types will be registered here (e.g., SnakeGameState)
})
public abstract class GameState {
    private UUID roomId;
    private int roundNumber;
    private int tickNumber;
    private GameStatus status;
    private Map<UUID, Object> playerStates;
}
