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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FunnyStatsGenerator Tests")
class FunnyStatsGeneratorTest {

    @Mock
    private GameResultRepository repository;

    private FunnyStatsGenerator generator;
    private List<GameResult> testResults;

    @BeforeEach
    void setUp() {
        generator = new FunnyStatsGenerator(repository);
        testResults = List.of(
            GameResult.builder().playerName("Alice").score(100).survivalTicks(50).foodEaten(10).build(),
            GameResult.builder().playerName("Alice").score(110).survivalTicks(55).foodEaten(11).build(),
            GameResult.builder().playerName("Bob").score(150).survivalTicks(75).foodEaten(15).build(),
            GameResult.builder().playerName("Bob").score(140).survivalTicks(70).foodEaten(14).build(),
            GameResult.builder().playerName("Charlie").score(80).survivalTicks(40).foodEaten(8).build(),
            GameResult.builder().playerName("Charlie").score(95).survivalTicks(48).foodEaten(9).build()
        );
    }

    @Test
    @DisplayName("Should generate all funny stats categories")
    void testGenerateAllFunnyStats() {
        when(repository.findTop100ByScore()).thenReturn(testResults);

        List<FunnyStats> stats = generator.generateAllFunnyStats();

        assertNotNull(stats);
        assertEquals(5, stats.size());
    }

    @Test
    @DisplayName("Should have correct stat titles")
    void testStatTitles() {
        when(repository.findTop100ByScore()).thenReturn(testResults);

        List<FunnyStats> stats = generator.generateAllFunnyStats();

        assertTrue(stats.stream().anyMatch(s -> s.getTitle().contains("Consistent")));
        assertTrue(stats.stream().anyMatch(s -> s.getTitle().contains("Improved")));
        assertTrue(stats.stream().anyMatch(s -> s.getTitle().contains("Survivors")));
        assertTrue(stats.stream().anyMatch(s -> s.getTitle().contains("Speedrunners")));
        assertTrue(stats.stream().anyMatch(s -> s.getTitle().contains("Greediest")));
    }

    @Test
    @DisplayName("Should have entries in stats")
    void testStatsHaveEntries() {
        when(repository.findTop100ByScore()).thenReturn(testResults);

        List<FunnyStats> stats = generator.generateAllFunnyStats();

        for (FunnyStats stat : stats) {
            assertNotNull(stat.getEntries());
        }
    }
}
