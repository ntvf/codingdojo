package me._on.codingdojo.server.game.snake;

import me._on.codingdojo.server.engine.map.MapConfig;
import me._on.codingdojo.server.model.GameState;
import me._on.codingdojo.server.model.GameStatus;
import me._on.codingdojo.server.model.Move;
import me._on.codingdojo.server.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnakeEngineTest {

    private SnakeEngine engine;
    private MapConfig mapConfig;
    private Room room;
    private UUID roomId;

    @BeforeEach
    void setUp() {
        engine = new SnakeEngine();
        roomId = UUID.randomUUID();

        room = new Room();
        room.setId(roomId);

        mapConfig = MapConfig.builder()
            .name("test")
            .type("snake")
            .width(20)
            .height(20)
            .config(new HashMap<>())
            .build();
    }

    @Test
    void testInitStateCreatesValidSnake() {
        GameState gameState = engine.initState(room, mapConfig);
        SnakeGameState state = (SnakeGameState) gameState;

        assertNotNull(state);
        assertEquals(roomId, state.getRoomId());
        assertEquals(GameStatus.IN_PROGRESS, state.getStatus());
        assertEquals(3, state.getSnakeBody().size());
        assertEquals(SnakeMove.RIGHT, state.getDirection());
        assertNotNull(state.getFoodLocation());
        assertEquals(0, state.getScore());
    }

    @Test
    void testInitStateSnakeStartsAtCenter() {
        GameState gameState = engine.initState(room, mapConfig);
        SnakeGameState state = (SnakeGameState) gameState;

        Point head = state.getSnakeBody().get(0);
        assertEquals(10, head.getX());
        assertEquals(10, head.getY());
    }

    @Test
    void testTickBasicMovement() {
        GameState gameState = engine.initState(room, mapConfig);
        SnakeGameState state = (SnakeGameState) gameState;
        Point initialHead = state.getSnakeBody().get(0);

        GameState nextState = engine.tick(gameState, Map.of());
        SnakeGameState updated = (SnakeGameState) nextState;

        assertEquals(new Point(initialHead.getX() + 1, initialHead.getY()),
                updated.getSnakeBody().get(0));
        assertEquals(3, updated.getSnakeBody().size());
    }

    @Test
    void testTickChangeDirection() {
        GameState gameState = engine.initState(room, mapConfig);
        Map<UUID, Move> moves = Map.of(UUID.randomUUID(), SnakeMove.UP);

        GameState nextState = engine.tick(gameState, moves);
        SnakeGameState updated = (SnakeGameState) nextState;

        assertEquals(SnakeMove.UP, updated.getDirection());
    }

    @Test
    void testTickPreventReverse() {
        GameState gameState = engine.initState(room, mapConfig);
        SnakeGameState state = (SnakeGameState) gameState;
        state.setDirection(SnakeMove.RIGHT);

        Map<UUID, Move> moves = Map.of(UUID.randomUUID(), SnakeMove.LEFT);
        GameState nextState = engine.tick(gameState, moves);
        SnakeGameState updated = (SnakeGameState) nextState;

        assertEquals(SnakeMove.RIGHT, updated.getDirection());
    }

    @Test
    void testTickWallCollision() {
        GameState gameState = engine.initState(room, mapConfig);
        SnakeGameState state = (SnakeGameState) gameState;

        state.getSnakeBody().clear();
        state.getSnakeBody().add(new Point(0, 10));
        state.setDirection(SnakeMove.LEFT);

        GameState nextState = engine.tick(gameState, Map.of());
        SnakeGameState updated = (SnakeGameState) nextState;

        assertEquals(GameStatus.FINISHED, updated.getStatus());
    }

    @Test
    void testTickSelfCollision() {
        GameState gameState = engine.initState(room, mapConfig);
        SnakeGameState state = (SnakeGameState) gameState;

        state.getSnakeBody().clear();
        state.getSnakeBody().add(new Point(10, 10));
        state.getSnakeBody().add(new Point(10, 11));
        state.getSnakeBody().add(new Point(11, 11));
        state.getSnakeBody().add(new Point(11, 10));
        state.setDirection(SnakeMove.DOWN);

        GameState nextState = engine.tick(gameState, Map.of());
        SnakeGameState updated = (SnakeGameState) nextState;

        assertEquals(GameStatus.FINISHED, updated.getStatus());
    }

    @Test
    void testTickFoodEating() {
        GameState gameState = engine.initState(room, mapConfig);
        SnakeGameState state = (SnakeGameState) gameState;

        Point head = state.getSnakeBody().get(0);
        state.setFoodLocation(head.move(SnakeMove.RIGHT));
        int initialScore = state.getScore();

        GameState nextState = engine.tick(gameState, Map.of());
        SnakeGameState updated = (SnakeGameState) nextState;

        assertEquals(initialScore + 1, updated.getScore());
        assertEquals(4, updated.getSnakeBody().size());
    }

    @Test
    void testIsRoundOverGameOverStatus() {
        GameState gameState = engine.initState(room, mapConfig);
        SnakeGameState state = (SnakeGameState) gameState;
        state.setStatus(GameStatus.FINISHED);

        assertTrue(engine.isRoundOver(gameState));
    }

    @Test
    void testIsRoundOverInProgress() {
        GameState gameState = engine.initState(room, mapConfig);
        assertFalse(engine.isRoundOver(gameState));
    }
}
