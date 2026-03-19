package me._on.codingdojo.server.stats;

import me._on.codingdojo.server.model.GameResult;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LeaderboardService Tests")
class LeaderboardServiceTest {

    @Mock
    private GameResultRepository repository;

    private LeaderboardService service;
    private UUID playerId;
    private List<GameResult> testResults;

    @BeforeEach
    void setUp() {
        service = new LeaderboardService(repository);
        playerId = UUID.randomUUID();
        testResults = List.of(
            GameResult.builder().playerId(playerId).playerName("Alice").score(100).build(),
            GameResult.builder().playerId(playerId).playerName("Alice").score(120).build(),
            GameResult.builder().playerId(playerId).playerName("Alice").score(110).build()
        );
    }

    @Test
    @DisplayName("Should return top scores")
    void testGetGlobalLeaderboard() {
        List<GameResult> mockTop100 = List.of(
            GameResult.builder().playerName("Bob").score(500).build(),
            GameResult.builder().playerName("Alice").score(450).build()
        );
        when(repository.findTop100ByScore()).thenReturn(mockTop100);

        List<GameResult> result = service.getGlobalLeaderboard(10);

        assertEquals(2, result.size());
        verify(repository, times(1)).findTop100ByScore();
    }

    @Test
    @DisplayName("Should calculate best score")
    void testGetPlayerBestScore() {
        when(repository.findByPlayerId(playerId)).thenReturn(testResults);

        int bestScore = service.getPlayerBestScore(playerId);

        assertEquals(120, bestScore);
    }

    @Test
    @DisplayName("Should calculate average score")
    void testGetPlayerAverageScore() {
        when(repository.findByPlayerId(playerId)).thenReturn(testResults);

        double avgScore = service.getPlayerAverageScore(playerId);

        assertEquals(110.0, avgScore);
    }

    @Test
    @DisplayName("Should count player games")
    void testGetPlayerGameCount() {
        when(repository.findByPlayerId(playerId)).thenReturn(testResults);

        int count = service.getPlayerGameCount(playerId);

        assertEquals(3, count);
    }

    @Test
    @DisplayName("Should return 0 for player with no games")
    void testGetBestScoreEmptyResults() {
        when(repository.findByPlayerId(playerId)).thenReturn(List.of());

        int bestScore = service.getPlayerBestScore(playerId);

        assertEquals(0, bestScore);
    }
}
