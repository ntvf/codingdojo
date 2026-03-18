package me._on.codingdojo.server.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me._on.codingdojo.server.config.GameConfig;
import me._on.codingdojo.server.model.GameState;
import me._on.codingdojo.server.model.Move;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameLoop {
    private final GameConfig gameConfig;
    private final Map<UUID, ScheduledFuture<?>> activeLoops = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    public void startGameLoop(UUID roomId, GameSession session) {
        long tickRateMs = gameConfig.getTickRateMs();
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(
            () -> tickGameLoop(session),
            0,
            tickRateMs,
            TimeUnit.MILLISECONDS
        );
        activeLoops.put(roomId, future);
        log.info("Started game loop for room {}", roomId);
    }

    public void stopGameLoop(UUID roomId) {
        ScheduledFuture<?> future = activeLoops.remove(roomId);
        if (future != null) {
            future.cancel(false);
            log.info("Stopped game loop for room {}", roomId);
        }
    }

    private void tickGameLoop(GameSession session) {
        try {
            Map<UUID, Move> moves = session.getAndClearPendingMoves();
            GameState newState = session.getGameEngine().tick(session.getCurrentState(), moves);
            session.updateState(newState);

            if (session.isRoundComplete()) {
                stopGameLoop(session.getRoomId());
                // Broadcast round completion (to be implemented)
            }
            // Broadcast new state to all players (to be implemented)
        } catch (Exception e) {
            log.error("Error in game loop for room {}", session.getRoomId(), e);
        }
    }
}
