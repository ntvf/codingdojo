package me._on.codingdojo.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me._on.codingdojo.server.model.GameState;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameStartedMessage {
    @Builder.Default
    private String type = "GAME_STARTED";
    private GameState state;
    @Builder.Default
    private long timestamp = System.currentTimeMillis();
}
