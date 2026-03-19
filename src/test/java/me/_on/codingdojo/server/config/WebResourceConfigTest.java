package me._on.codingdojo.server.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Web Resource Configuration Tests")
class WebResourceConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should handle static resource paths")
    void testStaticResourceHandling() throws Exception {
        mockMvc.perform(get("/static/test.css"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should be configured for resource serving")
    void testResourceServingConfiguration() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());
    }
}
