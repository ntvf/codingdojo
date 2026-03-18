package me._on.codingdojo.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

    private ServerApplication() {
        // Private constructor to hide implicit public one
    }

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
