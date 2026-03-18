package me._on.codingdojo.server.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GameConfigTest {

    @Autowired
    private GameConfig gameConfig;

    private static Validator validator;

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testDefaultValues() {
        GameConfig config = new GameConfig();
        assertEquals(50, config.getTickRateMs(), "Default tickRateMs should be 50");
        assertEquals(4, config.getMaxPlayers(), "Default maxPlayers should be 4");
        assertEquals(5, config.getDefaultRoundCount(), "Default defaultRoundCount should be 5");
    }

    @Test
    void testYamlBindingWithDefaults() {
        assertNotNull(gameConfig, "GameConfig should be injected");
        assertEquals(50, gameConfig.getTickRateMs(), "YAML tickRateMs should be 50");
        assertEquals(4, gameConfig.getMaxPlayers(), "YAML maxPlayers should be 4");
        assertEquals(5, gameConfig.getDefaultRoundCount(), "YAML defaultRoundCount should be 5");
    }

    @SpringBootTest
    @TestPropertySource(properties = {
            "dojo.game.tickRateMs=100",
            "dojo.game.maxPlayers=8",
            "dojo.game.defaultRoundCount=10"
    })
    static class CustomOverridesTest {
        @Autowired
        private GameConfig gameConfig;

        @Test
        void testCustomOverrides() {
            assertEquals(100, gameConfig.getTickRateMs(), "Custom tickRateMs should be 100");
            assertEquals(8, gameConfig.getMaxPlayers(), "Custom maxPlayers should be 8");
            assertEquals(10, gameConfig.getDefaultRoundCount(), "Custom defaultRoundCount should be 10");
        }
    }

    @Test
    void testValidationMinConstraints() {
        GameConfig config = new GameConfig();
        config.setTickRateMs(0);
        config.setMaxPlayers(0);
        config.setDefaultRoundCount(0);

        Set<ConstraintViolation<GameConfig>> violations = validator.validate(config);
        assertEquals(3, violations.size(), "Should have 3 validation errors for values < 1");
    }

    @Test
    void testValidationMaxConstraints() {
        GameConfig config = new GameConfig();
        config.setTickRateMs(1001);
        config.setMaxPlayers(101);
        config.setDefaultRoundCount(101);

        Set<ConstraintViolation<GameConfig>> violations = validator.validate(config);
        assertEquals(3, violations.size(), "Should have 3 validation errors for values exceeding max");
    }

    @Test
    void testValidationValidValues() {
        GameConfig config = new GameConfig();
        config.setTickRateMs(500);
        config.setMaxPlayers(50);
        config.setDefaultRoundCount(50);

        Set<ConstraintViolation<GameConfig>> violations = validator.validate(config);
        assertTrue(violations.isEmpty(), "Valid values should pass validation");
    }
}
