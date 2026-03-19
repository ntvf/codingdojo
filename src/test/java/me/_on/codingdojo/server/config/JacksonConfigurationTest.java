package me._on.codingdojo.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JacksonConfigurationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testInstantSerialization() throws Exception {
        Instant instant = Instant.parse("2024-01-15T10:30:00Z");
        String json = objectMapper.writeValueAsString(instant);
        assertNotNull(json);
        assertFalse(json.isEmpty());
        assertTrue(json.contains("."), "Serialized Instant should contain decimal point");
    }

    @Test
    void testLocalDateTimeSerialization() throws Exception {
        LocalDateTime dateTime = LocalDateTime.parse("2024-01-15T10:30:00");
        String json = objectMapper.writeValueAsString(dateTime);
        assertNotNull(json);
        assertFalse(json.isEmpty());
        assertTrue(json.contains("["), "Serialized LocalDateTime should be an array");
        assertTrue(json.contains("2024"), "Serialized LocalDateTime should contain year");
    }

    @Test
    void testCamelCaseNaming() throws Exception {
        TestModel model = new TestModel("John Doe", "john@example.com");
        String json = objectMapper.writeValueAsString(model);
        assertTrue(json.contains("\"firstName\""));
        assertTrue(json.contains("\"emailAddress\""));
        assertFalse(json.contains("\"first_name\""));
    }

    @Test
    void testObjectMapperSerializesObjects() {
        assertNotNull(objectMapper);
        TestModel testModel = new TestModel("John", "john@example.com");
        assertDoesNotThrow(() -> objectMapper.writeValueAsString(testModel));
    }

    static class TestModel {
        public String firstName;
        public String emailAddress;

        TestModel(String firstName, String emailAddress) {
            this.firstName = firstName;
            this.emailAddress = emailAddress;
        }
    }
}

