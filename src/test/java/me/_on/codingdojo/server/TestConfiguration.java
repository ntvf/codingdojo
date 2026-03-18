package me._on.codingdojo.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfiguration {
    // Add common test beans here if needed
    // Example: MockPlayerService, TestDataBuilder, etc.
}
