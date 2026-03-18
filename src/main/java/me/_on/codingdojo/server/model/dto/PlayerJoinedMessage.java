package me._on.codingdojo.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerJoinedMessage {
    @Builder.Default
    private String type = "PLAYER_JOINED";
    private UUID playerId;
    private String playerName;
    @Builder.Default
    private long timestamp = System.currentTimeMillis();
}
