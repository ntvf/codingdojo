package me._on.codingdojo.server.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Spring Configuration Beans Tests")
class ConfigurationBeansTest {

    @Autowired(required = false)
    private CorsConfig corsConfig;

    @Autowired(required = false)
    private WebResourceConfig webResourceConfig;

    @Test
    @DisplayName("CorsConfig bean should be created")
    void testCorsConfigBeanExists() {
        assertNotNull(corsConfig, "CorsConfig bean should be created");
    }

    @Test
    @DisplayName("WebResourceConfig bean should be created")
    void testWebResourceConfigBeanExists() {
        assertNotNull(webResourceConfig, "WebResourceConfig bean should be created");
    }

    @Test
    @DisplayName("CorsConfig should implement WebMvcConfigurer")
    void testCorsConfigImplementsWebMvcConfigurer() {
        assertTrue(corsConfig instanceof org.springframework.web.servlet.config.annotation.WebMvcConfigurer,
            "CorsConfig should implement WebMvcConfigurer");
    }

    @Test
    @DisplayName("WebResourceConfig should implement WebMvcConfigurer")
    void testWebResourceConfigImplementsWebMvcConfigurer() {
        assertTrue(webResourceConfig instanceof org.springframework.web.servlet.config.annotation.WebMvcConfigurer,
            "WebResourceConfig should implement WebMvcConfigurer");
    }
}
