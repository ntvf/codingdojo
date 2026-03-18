package me._on.codingdojo.server.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "dojo.game")
public class GameConfig {
    @Min(1)
    @Max(1000)
    private int tickRateMs = 50;

    @Min(1)
    @Max(100)
    private int maxPlayers = 4;

    @Min(1)
    @Max(100)
    private int defaultRoundCount = 5;
}
