package me._on.codingdojo.server.game.snake;

import me._on.codingdojo.server.model.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SnakeGameStateTest {

    private SnakeGameState state;
    private UUID testRoomId;

    @BeforeEach
    void setUp() {
        testRoomId = UUID.randomUUID();
        state = SnakeGameState.builder()
            .roomId(testRoomId)
            .status(GameStatus.IN_PROGRESS)
            .boardWidth(20)
            .boardHeight(20)
            .snakeBody(new ArrayList<>(List.of(
                new Point(10, 10),
                new Point(9, 10),
                new Point(8, 10)
            )))
            .direction(SnakeMove.RIGHT)
            .foodLocation(new Point(15, 15))
            .score(0)
            .build();
    }

    @Test
    void testInitialState() {
        assertNotNull(state.getSnakeBody());
        assertEquals(3, state.getSnakeBody().size());
        assertEquals(new Point(10, 10), state.getSnakeBody().get(0));
        assertEquals(SnakeMove.RIGHT, state.getDirection());
        assertEquals(0, state.getScore());
    }

    @Test
    void testSnakeGrowth() {
        state.getSnakeBody().add(0, new Point(11, 10));
        assertEquals(4, state.getSnakeBody().size());
    }

    @Test
    void testScoreIncrement() {
        state.setScore(state.getScore() + 1);
        assertEquals(1, state.getScore());
    }
}
