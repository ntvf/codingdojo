package me._on.codingdojo.server.game.snake;

import me._on.codingdojo.server.model.Move;

/**
 * Represents a snake movement direction in a game tick.
 */
public enum SnakeMove implements Move {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    /**
     * Check if this move is opposite (180 degrees) to another move.
     * Used to prevent snake from reversing into itself.
     */
    public boolean isOpposite(SnakeMove other) {
        return (this == UP && other == DOWN) ||
               (this == DOWN && other == UP) ||
               (this == LEFT && other == RIGHT) ||
               (this == RIGHT && other == LEFT);
    }
}
