package me._on.codingdojo.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(31536000);

        registry.addResourceHandler("/*.js", "/*.css", "/*.html", "/*.png", "/*.ico")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(31536000);
    }
}
