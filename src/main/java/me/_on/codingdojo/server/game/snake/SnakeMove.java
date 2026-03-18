package me._on.codingdojo.server.game.snake;

import me._on.codingdojo.server.model.Move;

public enum SnakeMove implements Move {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public boolean isOpposite(SnakeMove other) {
        return (this == UP && other == DOWN) ||
               (this == DOWN && other == UP) ||
               (this == LEFT && other == RIGHT) ||
               (this == RIGHT && other == LEFT);
    }
}
