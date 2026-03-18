package me._on.codingdojo.server.engine;

import me._on.codingdojo.server.model.GameState;
import me._on.codingdojo.server.model.Move;
import me._on.codingdojo.server.model.Round;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@DisplayName("GameSession Tests")
class GameSessionTest {

    private GameSession gameSession;
    private GameEngine mockGameEngine;
    private GameState mockGameState;
    private UUID roomId;
    private UUID playerId1;
    private UUID playerId2;

    @BeforeEach
    void setUp() {
        mockGameEngine = mock(GameEngine.class);
        mockGameState = mock(GameState.class);
        roomId = UUID.randomUUID();
        playerId1 = UUID.randomUUID();
        playerId2 = UUID.randomUUID();

        gameSession = new GameSession();
        gameSession.setRoomId(roomId);
        gameSession.setGameEngine(mockGameEngine);
        gameSession.setCurrentState(mockGameState);
        gameSession.setCreatedAt(System.currentTimeMillis());
    }

    @Test
    @DisplayName("submitMove should add move to pending moves")
    void testSubmitMove() {
        Move move = mock(Move.class);

        gameSession.submitMove(playerId1, move);

        assertEquals(1, gameSession.getPendingMoves().size());
        assertEquals(move, gameSession.getPendingMoves().get(playerId1));
    }

    @Test
    @DisplayName("submitMove should overwrite previous move from same player")
    void submitMoveOverwrite() {
        Move move1 = mock(Move.class);
        Move move2 = mock(Move.class);

        gameSession.submitMove(playerId1, move1);
        gameSession.submitMove(playerId1, move2);

        assertEquals(1, gameSession.getPendingMoves().size());
        assertEquals(move2, gameSession.getPendingMoves().get(playerId1));
    }

    @Test
    @DisplayName("getAndClearPendingMoves should return all moves and clear collection")
    void testGetAndClearPendingMoves() {
        Move move1 = mock(Move.class);
        Move move2 = mock(Move.class);

        gameSession.submitMove(playerId1, move1);
        gameSession.submitMove(playerId2, move2);

        Map<UUID, Move> moves = gameSession.getAndClearPendingMoves();

        assertEquals(2, moves.size());
        assertEquals(move1, moves.get(playerId1));
        assertEquals(move2, moves.get(playerId2));

        assertTrue(gameSession.getPendingMoves().isEmpty());
    }

    @Test
    @DisplayName("getAndClearPendingMoves should return empty map when no moves")
    void getAndClearPendingMovesEmpty() {
        Map<UUID, Move> moves = gameSession.getAndClearPendingMoves();

        assertTrue(moves.isEmpty());
    }

    @Test
    @DisplayName("updateState should update current state")
    void testUpdateState() {
        GameState newState = mock(GameState.class);

        gameSession.updateState(newState);

        assertEquals(newState, gameSession.getCurrentState());
    }

    @Test
    @DisplayName("isRoundComplete should return true when gameEngine says round is over")
    void isRoundCompleteTrue() {
        when(mockGameEngine.isRoundOver(mockGameState)).thenReturn(true);

        boolean isComplete = gameSession.isRoundComplete();

        assertTrue(isComplete);
        verify(mockGameEngine).isRoundOver(mockGameState);
    }

    @Test
    @DisplayName("isRoundComplete should return false when gameEngine says round is not over")
    void isRoundCompleteFalse() {
        when(mockGameEngine.isRoundOver(mockGameState)).thenReturn(false);

        boolean isComplete = gameSession.isRoundComplete();

        assertFalse(isComplete);
        verify(mockGameEngine).isRoundOver(mockGameState);
    }

    @Test
    @DisplayName("submitMove should be thread-safe with concurrent submissions")
    void submitMoveThreadSafe() throws InterruptedException {
        int threadCount = 10;
        int movesPerThread = 100;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int threadId = i;
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < movesPerThread; j++) {
                        UUID playerId = UUID.nameUUIDFromBytes(("player-" + threadId).getBytes());
                        Move move = mock(Move.class);
                        gameSession.submitMove(playerId, move);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown();
        endLatch.await();

        assertTrue(gameSession.getPendingMoves().size() <= threadCount);
    }

    @Test
    @DisplayName("getAndClearPendingMoves should be thread-safe during concurrent read-write")
    void getAndClearPendingMovesThreadSafe() throws InterruptedException {
        AtomicInteger successCount = new AtomicInteger(0);
        int operationCount = 50;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(operationCount);

        for (int i = 0; i < operationCount; i++) {
            int opId = i;
            new Thread(() -> {
                try {
                    startLatch.await();
                    if (opId % 2 == 0) {
                        UUID playerId = UUID.nameUUIDFromBytes(("player-" + opId).getBytes());
                        Move move = mock(Move.class);
                        gameSession.submitMove(playerId, move);
                    } else {
                        gameSession.getAndClearPendingMoves();
                    }
                    successCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown();
        endLatch.await();

        assertEquals(operationCount, successCount.get());
    }

    @Test
    @DisplayName("GameSession constructor with all args should set all fields")
    void constructorAllArgs() {
        Round round = new Round();
        long createdAt = System.currentTimeMillis();

        gameSession = new GameSession(roomId, mockGameEngine, mockGameState, round, createdAt);

        assertEquals(roomId, gameSession.getRoomId());
        assertEquals(mockGameEngine, gameSession.getGameEngine());
        assertEquals(mockGameState, gameSession.getCurrentState());
        assertEquals(round, gameSession.getCurrentRound());
        assertEquals(createdAt, gameSession.getCreatedAt());
    }
}
