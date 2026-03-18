package me._on.codingdojo.server.game.snake;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import me._on.codingdojo.server.model.GameState;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@JsonTypeName("snake")
public class SnakeGameState extends GameState {
    // Snake body positions: list of coordinates, head is first element
    private List<Point> snakeBody = new ArrayList<>();

    // Current direction snake is moving
    private SnakeMove direction = SnakeMove.RIGHT;

    // Food position
    private Point foodLocation;

    // Score/eat count
    private int score = 0;

    // Board dimensions
    private int boardWidth;
    private int boardHeight;
}
