package me._on.codingdojo.server.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameConfigTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testDefaultValues() {
        GameConfig config = new GameConfig();
        assertEquals(50, config.getTickRateMs());
        assertEquals(4, config.getMaxPlayers());
        assertEquals(5, config.getDefaultRoundCount());
    }

    @Test
    void testValidationMinConstraints() {
        GameConfig config = new GameConfig();
        config.setTickRateMs(0);
        config.setMaxPlayers(0);
        config.setDefaultRoundCount(0);

        Set<ConstraintViolation<GameConfig>> violations = validator.validate(config);
        assertEquals(3, violations.size());
    }

    @Test
    void testValidationMaxConstraints() {
        GameConfig config = new GameConfig();
        config.setTickRateMs(1001);
        config.setMaxPlayers(101);
        config.setDefaultRoundCount(101);

        Set<ConstraintViolation<GameConfig>> violations = validator.validate(config);
        assertEquals(3, violations.size());
    }

    @Test
    void testValidationValidValues() {
        GameConfig config = new GameConfig();
        config.setTickRateMs(500);
        config.setMaxPlayers(50);
        config.setDefaultRoundCount(50);

        Set<ConstraintViolation<GameConfig>> violations = validator.validate(config);
        assertTrue(violations.isEmpty());
    }
}

