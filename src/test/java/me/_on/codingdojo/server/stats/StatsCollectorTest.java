package me._on.codingdojo.server.stats;

import me._on.codingdojo.server.game.snake.Point;
import me._on.codingdojo.server.game.snake.SnakeGameState;
import me._on.codingdojo.server.game.snake.SnakeMove;
import me._on.codingdojo.server.model.GameResult;
import me._on.codingdojo.server.model.Player;
import me._on.codingdojo.server.model.repository.GameResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StatsCollector Tests")
class StatsCollectorTest {

    @Mock
    private GameResultRepository repository;

    private StatsCollector collector;
    private UUID roomId;
    private UUID playerId;
    private Player player;
    private SnakeGameState gameState;

    @BeforeEach
    void setUp() {
        collector = new StatsCollector(repository);
        roomId = UUID.randomUUID();
        playerId = UUID.randomUUID();
        player = Player.builder()
            .id(playerId)
            .name("TestPlayer")
            .build();
        gameState = SnakeGameState.builder()
            .roomId(roomId)
            .snakeBody(List.of(new Point(10, 10)))
            .direction(SnakeMove.RIGHT)
            .food(new Point(15, 15))
            .score(100)
            .build();
    }

    @Test
    @DisplayName("Should record game result with correct score")
    void testRecordGameResult() {
        when(repository.save(any(GameResult.class))).thenAnswer(invocation -> {
            GameResult arg = invocation.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        GameResult result = collector.recordGameResult(roomId, player, gameState, 5000L);

        assertNotNull(result);
        assertEquals(100, result.getScore());
        assertEquals(playerId, result.getPlayerId());
        assertEquals("TestPlayer", result.getPlayerName());
        verify(repository, times(1)).save(any(GameResult.class));
    }

    @Test
    @DisplayName("Should calculate survival ticks correctly")
    void testSurvivalTicks() {
        when(repository.save(any(GameResult.class))).thenAnswer(invocation -> {
            GameResult arg = invocation.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        GameResult result = collector.recordGameResult(roomId, player, gameState, 4000L);

        assertEquals(20, result.getSurvivalTicks());
    }

    @Test
    @DisplayName("Should record game result with overloaded method")
    void testRecordGameResultOverloaded() {
        when(repository.save(any(GameResult.class))).thenAnswer(invocation -> {
            GameResult arg = invocation.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        GameResult result = collector.recordGameResult(roomId, "Alice", playerId, 150, 15);

        assertNotNull(result);
        assertEquals(150, result.getScore());
        assertEquals(15, result.getFoodEaten());
        assertEquals("Alice", result.getPlayerName());
    }
}
