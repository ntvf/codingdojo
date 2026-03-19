package me._on.codingdojo.server.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import me._on.codingdojo.server.model.GameResult;
import me._on.codingdojo.server.model.repository.GameResultRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final GameResultRepository gameResultRepository;

    public List<GameResult> getGlobalLeaderboard(int limit) {
        return gameResultRepository.findTop100ByScore()
            .stream()
            .limit(limit)
            .collect(Collectors.toList());
    }

    public List<GameResult> getPlayerStats(UUID playerId) {
        return gameResultRepository.findByPlayerId(playerId);
    }

    public List<GameResult> getRoomResults(UUID roomId) {
        return gameResultRepository.findByRoomId(roomId);
    }

    public int getPlayerBestScore(UUID playerId) {
        return getPlayerStats(playerId)
            .stream()
            .mapToInt(GameResult::getScore)
            .max()
            .orElse(0);
    }

    public double getPlayerAverageScore(UUID playerId) {
        List<GameResult> results = getPlayerStats(playerId);
        if (results.isEmpty()) {
            return 0;
        }
        return results.stream()
            .mapToInt(GameResult::getScore)
            .average()
            .orElse(0);
    }

    public int getPlayerGameCount(UUID playerId) {
        return (int) getPlayerStats(playerId).size();
    }
}
