package me._on.codingdojo.server.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me._on.codingdojo.server.model.GameResult;
import me._on.codingdojo.server.model.dto.GameResultResponse;
import me._on.codingdojo.server.model.dto.LeaderboardEntryResponse;
import me._on.codingdojo.server.stats.FunnyStats;
import me._on.codingdojo.server.stats.FunnyStatsGenerator;
import me._on.codingdojo.server.stats.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final LeaderboardService leaderboardService;
    private final FunnyStatsGenerator funnyStatsGenerator;

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryResponse>> getLeaderboard(
            @RequestParam(defaultValue = "100") int limit) {
        List<GameResult> topScores = leaderboardService.getGlobalLeaderboard(limit);

        List<LeaderboardEntryResponse> entries = topScores.stream()
            .map((GameResult result) -> {
                int rank = topScores.indexOf(result) + 1;
                return LeaderboardEntryResponse.builder()
                    .rank(rank)
                    .playerId(result.getPlayerId())
                    .playerName(result.getPlayerName())
                    .score(result.getScore())
                    .gameCount(leaderboardService.getPlayerGameCount(result.getPlayerId()))
                    .averageScore(leaderboardService.getPlayerAverageScore(result.getPlayerId()))
                    .bestScore(leaderboardService.getPlayerBestScore(result.getPlayerId()))
                    .build();
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(entries);
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<Object> getPlayerStats(@PathVariable UUID playerId) {
        List<GameResult> results = leaderboardService.getPlayerStats(playerId);

        if (results.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var stats = new Object() {
            public String playerName = results.get(0).getPlayerName();
            public int gameCount = results.size();
            public int bestScore = leaderboardService.getPlayerBestScore(playerId);
            public double averageScore = leaderboardService.getPlayerAverageScore(playerId);
            public List<GameResultResponse> games = results.stream()
                .map(r -> GameResultResponse.builder()
                    .id(r.getId())
                    .roomId(r.getRoomId())
                    .playerId(r.getPlayerId())
                    .playerName(r.getPlayerName())
                    .gameType(r.getGameType())
                    .score(r.getScore())
                    .survivalTicks(r.getSurvivalTicks())
                    .foodEaten(r.getFoodEaten())
                    .completedAt(r.getCompletedAt())
                    .build())
                .collect(Collectors.toList());
        };

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<GameResultResponse>> getRoomResults(@PathVariable UUID roomId) {
        List<GameResult> results = leaderboardService.getRoomResults(roomId);

        List<GameResultResponse> responses = results.stream()
            .map(r -> GameResultResponse.builder()
                .id(r.getId())
                .roomId(r.getRoomId())
                .playerId(r.getPlayerId())
                .playerName(r.getPlayerName())
                .gameType(r.getGameType())
                .score(r.getScore())
                .survivalTicks(r.getSurvivalTicks())
                .foodEaten(r.getFoodEaten())
                .completedAt(r.getCompletedAt())
                .build())
            .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/funny")
    public ResponseEntity<List<FunnyStats>> getFunnyStats() {
        List<FunnyStats> stats = funnyStatsGenerator.generateAllFunnyStats();
        return ResponseEntity.ok(stats);
    }
}
