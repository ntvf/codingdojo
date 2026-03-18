package me._on.codingdojo.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfiguration {

@Bean
public JavaTimeModule javaTimeModule() {
return new JavaTimeModule();
}

@Bean
@Primary
public ObjectMapper objectMapper(JavaTimeModule javaTimeModule) {
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(javaTimeModule);
mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
return mapper;
}
}
