package me._on.codingdojo.server.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import me._on.codingdojo.server.AbstractIntegrationTest;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SpaController Tests")
class SpaControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should forward root to index.html")
    void testRootForwardToIndex() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    @DisplayName("Should forward single path segment to index.html")
    void testSinglePathSegment() throws Exception {
        mockMvc.perform(get("/room/ABC-DEF"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    @DisplayName("Should forward nested path to index.html")
    void testNestedPath() throws Exception {
        mockMvc.perform(get("/game/ABC-DEF/play"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    @DisplayName("Should forward help page to index.html")
    void testHelpPageForward() throws Exception {
        mockMvc.perform(get("/help"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    @DisplayName("Should forward settings page to index.html")
    void testSettingsPageForward() throws Exception {
        mockMvc.perform(get("/settings"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    @DisplayName("Should forward profile page to index.html")
    void testProfilePageForward() throws Exception {
        mockMvc.perform(get("/profile"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    @DisplayName("Should NOT forward API routes - API endpoints take precedence")
    void testApiRoutesNotForwarded() throws Exception {
        mockMvc.perform(get("/api/stats/leaderboard"))
            .andExpect(status().isOk())
            .andExpect(header().exists("Content-Type"));
    }

    @Test
    @DisplayName("Should forward about page to index.html")
    void testAboutPageForward() throws Exception {
        mockMvc.perform(get("/about"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    @DisplayName("Should forward game details with complex ID to index.html")
    void testGameDetailsForward() throws Exception {
        mockMvc.perform(get("/game/room-123-abc/edit"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/index.html"));
    }
}





