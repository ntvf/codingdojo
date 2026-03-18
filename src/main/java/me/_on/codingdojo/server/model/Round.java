package me._on.codingdojo.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Round {
    private int roundNumber;
    private Object mapConfig;
    private int duration;
    private int speed;
    private Map<UUID, Integer> scores;
}
