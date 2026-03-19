package me._on.codingdojo.server.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameState {

    @JsonProperty("state")
    private GameStateData state;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameStateData {
        private String roomId;
        private String type;
        private int score;
        private long timestamp;
    }
}
