package me._on.codingdojo.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import me._on.codingdojo.server.model.GameState;

@Data
@AllArgsConstructor
public class GameStateResponse {
    private GameState state;
}
