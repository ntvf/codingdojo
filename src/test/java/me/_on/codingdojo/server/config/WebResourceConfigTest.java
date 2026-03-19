package me._on.codingdojo.server.config;

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
@DisplayName("Web Resource Configuration Tests")
class WebResourceConfigTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should serve root index page")
    void testIndexPageServing() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    @DisplayName("Should have web resource configuration active")
    void testWebResourceConfigurationActive() throws Exception {
        mockMvc.perform(get("/app"))
            .andExpect(status().isOk());
    }
}
