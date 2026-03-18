package me._on.codingdojo.server.game.snake;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnakeMoveTest {

    @Test
    void testIsOppositeUpDown() {
        assertTrue(SnakeMove.UP.isOpposite(SnakeMove.DOWN));
        assertTrue(SnakeMove.DOWN.isOpposite(SnakeMove.UP));
    }

    @Test
    void testIsOppositeLeftRight() {
        assertTrue(SnakeMove.LEFT.isOpposite(SnakeMove.RIGHT));
        assertTrue(SnakeMove.RIGHT.isOpposite(SnakeMove.LEFT));
    }

    @Test
    void testIsOppositeNonOpposite() {
        assertFalse(SnakeMove.UP.isOpposite(SnakeMove.LEFT));
        assertFalse(SnakeMove.RIGHT.isOpposite(SnakeMove.DOWN));
        assertFalse(SnakeMove.UP.isOpposite(SnakeMove.RIGHT));
        assertFalse(SnakeMove.DOWN.isOpposite(SnakeMove.LEFT));
    }

    @Test
    void testIsOppositeSameDirection() {
        assertFalse(SnakeMove.UP.isOpposite(SnakeMove.UP));
        assertFalse(SnakeMove.DOWN.isOpposite(SnakeMove.DOWN));
    }
}
