package me._on.codingdojo.server.game.snake;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PointTest {

    @Test
    void testMoveUp() {
        Point p = new Point(5, 5);
        Point moved = p.move(SnakeMove.UP);
        assertEquals(new Point(5, 4), moved);
    }

    @Test
    void testMoveDown() {
        Point p = new Point(5, 5);
        Point moved = p.move(SnakeMove.DOWN);
        assertEquals(new Point(5, 6), moved);
    }

    @Test
    void testMoveLeft() {
        Point p = new Point(5, 5);
        Point moved = p.move(SnakeMove.LEFT);
        assertEquals(new Point(4, 5), moved);
    }

    @Test
    void testMoveRight() {
        Point p = new Point(5, 5);
        Point moved = p.move(SnakeMove.RIGHT);
        assertEquals(new Point(6, 5), moved);
    }

    @Test
    void testEqualsSame() {
        assertEquals(new Point(5, 5), new Point(5, 5));
    }

    @Test
    void testEqualsDifferent() {
        assertNotEquals(new Point(5, 5), new Point(5, 6));
        assertNotEquals(new Point(5, 5), new Point(6, 5));
    }

    @Test
    void testHashCodeSame() {
        Point p1 = new Point(5, 5);
        Point p2 = new Point(5, 5);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
