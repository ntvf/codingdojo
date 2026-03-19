package me._on.codingdojo.server.client.bots;

import lombok.extern.slf4j.Slf4j;
import me._on.codingdojo.server.client.GameClient;
import me._on.codingdojo.server.client.GameEvent;
import me._on.codingdojo.server.client.GameState;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SnakeBotAI implements AutoCloseable {

    private final GameClient gameClient;
    private final String botName;
    private final long thinkTimeMs;
    private volatile boolean running = false;
    private CompletableFuture<Void> gameLoop;
    private String lastDirection = "RIGHT";
    private final Random random = new Random();

    public SnakeBotAI(GameClient gameClient, String botName, long thinkTimeMs) {
        this.gameClient = gameClient;
        this.botName = botName;
        this.thinkTimeMs = thinkTimeMs;
    }

    public CompletableFuture<Void> play(String roomCode) {
        return gameClient.connect(roomCode, botName)
            .thenRun(() -> {
                running = true;
                setupCallbacks();
                gameLoop = startGameLoop();
                log.info("{} connected to {}", botName, roomCode);
            })
            .thenCompose(v -> gameLoop);
    }

    private void setupCallbacks() {
        gameClient.onGameStateUpdate(state -> {
            log.debug("{} received state update: score={}", botName,
                state.getState().getScore());
        });

        gameClient.onGameEvent(event -> {
            log.info("{} received event: type={}", botName, event.getType());
            if (event.getType() == GameEvent.EventType.GAME_ENDED) {
                running = false;
            }
        });
    }

    private CompletableFuture<Void> startGameLoop() {
        return CompletableFuture.runAsync(() -> {
            while (running) {
                try {
                    GameState state = gameClient.getGameState()
                        .get(5, TimeUnit.SECONDS);

                    String nextMove = decideMove(state);

                    gameClient.submitMove(nextMove)
                        .get(5, TimeUnit.SECONDS);

                    lastDirection = nextMove;

                    Thread.sleep(thinkTimeMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("{} error in game loop: {}", botName, e.getMessage());
                    running = false;
                }
            }

            gameClient.disconnect();
            log.info("{} game loop finished", botName);
        });
    }

    private String decideMove(GameState state) {
        String[] validMoves = { "UP", "DOWN", "LEFT", "RIGHT" };

        String opposite = getOppositeDirection(lastDirection);

        String move;
        do {
            move = validMoves[random.nextInt(validMoves.length)];
        } while (move.equals(opposite));

        return move;
    }

    private String getOppositeDirection(String direction) {
        return switch (direction) {
            case "UP" -> "DOWN";
            case "DOWN" -> "UP";
            case "LEFT" -> "RIGHT";
            case "RIGHT" -> "LEFT";
            default -> "";
        };
    }

    @Override
    public void close() {
        running = false;
        if (gameLoop != null) {
            gameLoop.cancel(true);
        }
        gameClient.disconnect();
    }
}
