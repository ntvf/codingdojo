package me._on.codingdojo.server.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "host_player_id")
    private UUID hostPlayerId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @Column(name = "game_type")
    private String gameType;

    @Column(columnDefinition = "jsonb")
    private Map<String, Object> settings;

    @Column(columnDefinition = "jsonb")
    private List<UUID> playerIds;
}
