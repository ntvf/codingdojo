package me._on.codingdojo.server.api.error;

public class GameNotStartedException extends RuntimeException {
    public GameNotStartedException(String message) {
        super(message);
    }

    public GameNotStartedException(String message, Throwable cause) {
        super(message, cause);
    }
}
