package me._on.codingdojo.server.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me._on.codingdojo.server.game.snake.SnakeGameState;
import me._on.codingdojo.server.model.GameResult;
import me._on.codingdojo.server.model.Player;
import me._on.codingdojo.server.model.repository.GameResultRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsCollector {

    private final GameResultRepository gameResultRepository;

    public GameResult recordGameResult(UUID roomId, Player player, SnakeGameState finalState, long durationMs) {
        GameResult result = GameResult.builder()
            .roomId(roomId)
            .playerId(player.getId())
            .playerName(player.getName())
            .gameType("snake")
            .score(finalState.getScore())
            .survivalTicks((int) (durationMs / 200))
            .finalDirection(finalState.getDirection().toString())
            .foodEaten(finalState.getScore() / 10)
            .completedAt(System.currentTimeMillis())
            .build();

        GameResult saved = gameResultRepository.save(result);
        log.info("Recorded game result: player={}, score={}, survival={}ms",
            player.getName(), finalState.getScore(), durationMs);
        return saved;
    }

    public GameResult recordGameResult(UUID roomId, String playerName, UUID playerId, int score, int foodEaten) {
        GameResult result = GameResult.builder()
            .roomId(roomId)
            .playerId(playerId)
            .playerName(playerName)
            .gameType("snake")
            .score(score)
            .foodEaten(foodEaten)
            .completedAt(System.currentTimeMillis())
            .build();

        return gameResultRepository.save(result);
    }
}
