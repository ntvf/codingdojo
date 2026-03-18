package me._on.codingdojo.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private UUID id;
    private String code;
    private String name;
    private UUID hostPlayerId;
    private RoomStatus status;
    private String gameType;
    private Map<String, Object> settings;
    private List<UUID> playerIds;
}
