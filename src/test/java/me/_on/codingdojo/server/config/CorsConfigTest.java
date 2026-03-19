package me._on.codingdojo.server.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import me._on.codingdojo.server.AbstractIntegrationTest;

@DisplayName("CORS Configuration Tests")
class CorsConfigTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should include CORS headers on preflight requests from localhost:5173")
    void testCorsHeadersForLocalhost5173() throws Exception {
        mockMvc.perform(options("/api/stats")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isOk())
            .andExpect(header().exists("Access-Control-Allow-Origin"))
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
            .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
            .andExpect(header().exists("Access-Control-Allow-Methods"));
    }

    @Test
    @DisplayName("Should include CORS headers on preflight requests from localhost:3000")
    void testCorsHeadersForLocalhost3000() throws Exception {
        mockMvc.perform(options("/api/stats")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isOk())
            .andExpect(header().exists("Access-Control-Allow-Origin"))
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    @DisplayName("Should include CORS headers on preflight requests from 127.0.0.1:5173")
    void testCorsHeadersForLoopback5173() throws Exception {
        mockMvc.perform(options("/api/stats")
                .header("Origin", "http://127.0.0.1:5173")
                .header("Access-Control-Request-Method", "POST"))
            .andExpect(status().isOk())
            .andExpect(header().exists("Access-Control-Allow-Origin"))
            .andExpect(header().string("Access-Control-Allow-Origin", "http://127.0.0.1:5173"));
    }

    @Test
    @DisplayName("Should include CORS headers on preflight requests from 127.0.0.1:3000")
    void testCorsHeadersForLoopback3000() throws Exception {
        mockMvc.perform(options("/api/game")
                .header("Origin", "http://127.0.0.1:3000")
                .header("Access-Control-Request-Method", "POST"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://127.0.0.1:3000"));
    }

    @Test
    @DisplayName("Should allow credentials in CORS")
    void testCorsCredentials() throws Exception {
        mockMvc.perform(options("/api/game")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    @DisplayName("Should include allowed methods in preflight response")
    void testCorsAllowedMethods() throws Exception {
        mockMvc.perform(options("/api/stats")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "PUT"))
            .andExpect(status().isOk())
            .andExpect(header().exists("Access-Control-Allow-Methods"))
            .andExpect(header().exists("Access-Control-Max-Age"));
    }

    @Test
    @DisplayName("Should reject CORS requests from disallowed origins")
    void testCorsRejectUnknownOrigin() throws Exception {
        mockMvc.perform(options("/api/stats")
                .header("Origin", "http://evil.com")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should reject CORS requests from different port on localhost")
    void testCorsRejectDifferentPort() throws Exception {
        mockMvc.perform(options("/api/stats")
                .header("Origin", "http://localhost:8080")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow CORS for WebSocket endpoint from allowed origin")
    void testCorsWebSocket() throws Exception {
        mockMvc.perform(options("/ws")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isOk())
            .andExpect(header().exists("Access-Control-Allow-Origin"));
    }
}
