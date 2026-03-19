package me._on.codingdojo.server.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameEvent {
    public enum EventType {
        GAME_STARTED,
        GAME_ENDED,
        PLAYER_JOINED,
        PLAYER_LEFT,
        ERROR
    }

    private EventType type;
    private String message;
    private long timestamp;
}
