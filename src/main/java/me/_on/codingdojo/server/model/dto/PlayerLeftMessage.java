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
public class PlayerLeftMessage {
    @Builder.Default
    private String type = "PLAYER_LEFT";
    private UUID playerId;
    @Builder.Default
    private long timestamp = System.currentTimeMillis();
}
