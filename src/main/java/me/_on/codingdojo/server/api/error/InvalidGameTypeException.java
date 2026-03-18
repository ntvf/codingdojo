package me._on.codingdojo.server.api.error;

public class InvalidGameTypeException extends RuntimeException {
    public InvalidGameTypeException(String message) {
        super(message);
    }

    public InvalidGameTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
