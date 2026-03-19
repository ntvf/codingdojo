package me._on.codingdojo.server.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import me._on.codingdojo.server.model.GameResult;
import me._on.codingdojo.server.model.repository.GameResultRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FunnyStatsGenerator {

    private final GameResultRepository gameResultRepository;

    public List<FunnyStats> generateAllFunnyStats() {
        List<GameResult> allResults = gameResultRepository.findTop100ByScore();

        return List.of(
            generateMostConsistentPlayer(allResults),
            generateMostImprover(allResults),
            generateLongestSurvival(allResults),
            generateFastestWinner(allResults),
            generateGreediestPlayer(allResults)
        );
    }

    private FunnyStats generateMostConsistentPlayer(List<GameResult> results) {
        var grouped = results.stream()
            .collect(Collectors.groupingBy(
                GameResult::getPlayerName,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    games -> {
                        if (games.isEmpty()) {
                            return 0.0;
                        }
                        double avg = games.stream()
                            .mapToInt(GameResult::getScore)
                            .average()
                            .orElse(0);
                        double variance = games.stream()
                            .mapToDouble(g -> Math.pow(g.getScore() - avg, 2))
                            .average()
                            .orElse(0);
                        return Math.sqrt(variance);
                    }
                )
            ))
            .entrySet().stream()
            .sorted(Comparator.comparingDouble(e -> e.getValue()))
            .limit(5)
            .map(e -> FunnyStats.FunnyStatEntry.builder()
                .playerName(e.getKey())
                .value(String.format("σ=%.1f", e.getValue()))
                .build())
            .collect(Collectors.toList());

        return FunnyStats.builder()
            .title("🎯 Most Consistent Players")
            .description("Lowest score variance (most predictable gameplay)")
            .entries(grouped)
            .build();
    }

    private FunnyStats generateMostImprover(List<GameResult> results) {
        var grouped = results.stream()
            .collect(Collectors.groupingBy(GameResult::getPlayerName))
            .entrySet().stream()
            .map(e -> {
                List<GameResult> games = e.getValue();
                if (games.size() < 2) {
                    return null;
                }
                int first = games.get(0).getScore();
                int last = games.get(games.size() - 1).getScore();
                int improvement = last - first;
                return new Object[]{e.getKey(), improvement};
            })
            .filter(e -> e != null && (Integer) e[1] > 0)
            .sorted(Comparator.<Object[]>comparingInt(e -> (Integer) e[1]).reversed())
            .limit(5)
            .map(e -> FunnyStats.FunnyStatEntry.builder()
                .playerName((String) e[0])
                .value(String.format("+%d points", (Integer) e[1]))
                .build())
            .collect(Collectors.toList());

        return FunnyStats.builder()
            .title("📈 Most Improved Players")
            .description("Biggest score increase between first and last game")
            .entries(grouped)
            .build();
    }

    private FunnyStats generateLongestSurvival(List<GameResult> results) {
        var top = results.stream()
            .sorted(Comparator.comparingInt(GameResult::getSurvivalTicks).reversed())
            .limit(5)
            .map(r -> FunnyStats.FunnyStatEntry.builder()
                .playerName(r.getPlayerName())
                .value(String.format("%d ticks (%.1fs)", r.getSurvivalTicks(), r.getSurvivalTicks() * 0.2))
                .build())
            .collect(Collectors.toList());

        return FunnyStats.builder()
            .title("⏱️ Longest Survivors")
            .description("Ticks survived before collision")
            .entries(top)
            .build();
    }

    private FunnyStats generateFastestWinner(List<GameResult> results) {
        var top = results.stream()
            .filter(r -> r.getScore() > 100)
            .sorted(Comparator.comparingInt(GameResult::getSurvivalTicks))
            .limit(5)
            .map(r -> FunnyStats.FunnyStatEntry.builder()
                .playerName(r.getPlayerName())
                .value(String.format("Score %d in %.1fs", r.getScore(), r.getSurvivalTicks() * 0.2))
                .build())
            .collect(Collectors.toList());

        return FunnyStats.builder()
            .title("⚡ Speedrunners")
            .description("Fastest to reach 100+ score")
            .entries(top)
            .build();
    }

    private FunnyStats generateGreediestPlayer(List<GameResult> results) {
        var top = results.stream()
            .sorted(Comparator.comparingInt(GameResult::getFoodEaten).reversed())
            .limit(5)
            .map(r -> FunnyStats.FunnyStatEntry.builder()
                .playerName(r.getPlayerName())
                .value(String.format("%d food pellets 🍎", r.getFoodEaten()))
                .build())
            .collect(Collectors.toList());

        return FunnyStats.builder()
            .title("🥘 Greediest Players")
            .description("Most food pellets eaten in a single game")
            .entries(top)
            .build();
    }
}
