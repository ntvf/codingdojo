package me._on.codingdojo.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me._on.codingdojo.server.AbstractIntegrationTest;

class JacksonConfigurationTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JavaTimeModule javaTimeModule;

    @Test
    void testJavaTimeModuleIsRegistered() {
        assertNotNull(javaTimeModule, "JavaTimeModule should be registered as a bean");
    }

    @Test
    void testInstantSerialization() throws Exception {
        Instant instant = Instant.parse("2024-01-15T10:30:00Z");
        String json = objectMapper.writeValueAsString(instant);
        assertNotNull(json);
        assertFalse(json.isEmpty(), "Serialized Instant should not be empty");
        // Instant is serialized in nanosecond precision with JavaTimeModule
        assertTrue(json.contains("."), "Serialized Instant should contain decimal point (nanoseconds)");
        assertTrue(json.matches("^\\d+\\.\\d+$"), "Serialized Instant should be numeric with nanoseconds");
    }

    @Test
    void testLocalDateTimeSerialization() throws Exception {
        LocalDateTime dateTime = LocalDateTime.parse("2024-01-15T10:30:00");
        String json = objectMapper.writeValueAsString(dateTime);
        assertNotNull(json);
        assertFalse(json.isEmpty(), "Serialized LocalDateTime should not be empty");
        // LocalDateTime is serialized as an array [year, month, day, hour, minute]
        assertTrue(json.contains("["), "Serialized LocalDateTime should be an array");
        assertTrue(json.contains("2024"), "Serialized LocalDateTime should contain year");
    }

    @Test
    void testCamelCaseNaming() throws Exception {
        TestModel model = new TestModel("John Doe", "john@example.com");
        String json = objectMapper.writeValueAsString(model);

        assertTrue(json.contains("\"firstName\""), "JSON should contain camelCase 'firstName'");
        assertTrue(json.contains("\"emailAddress\""), "JSON should contain camelCase 'emailAddress'");
        assertFalse(json.contains("\"first_name\""), "JSON should not contain snake_case 'first_name'");
        assertFalse(json.contains("\"email_address\""), "JSON should not contain snake_case 'email_address'");
    }

    @Test
    void testPropertyNamingStrategyIsLowerCamelCase() {
        var strategy = objectMapper.getSerializationConfig().getPropertyNamingStrategy();
        assertNotNull(strategy, "Property naming strategy should be configured");
        assertEquals("LowerCamelCaseStrategy", strategy.getClass().getSimpleName(),
                "Property naming strategy should be LowerCamelCase");
    }

    @Test
    void testObjectMapperIsConfigured() {
        assertNotNull(objectMapper, "ObjectMapper bean should be configured");
        // Verify that the ObjectMapper can be used (implicitly configured)
        TestModel testModel = new TestModel("John", "john@example.com");
        assertDoesNotThrow(() -> objectMapper.writeValueAsString(testModel),
                "ObjectMapper should be able to serialize objects");
    }

    // Test model for camelCase naming validation
    static class TestModel {
        public String firstName;
        public String emailAddress;

        TestModel(String firstName, String emailAddress) {
            this.firstName = firstName;
            this.emailAddress = emailAddress;
        }
    }
}
