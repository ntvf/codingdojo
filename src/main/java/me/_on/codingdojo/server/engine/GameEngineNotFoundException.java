package me._on.codingdojo.server.engine;

/**
 * Exception thrown when a requested game engine is not registered in the registry.
 */
public class GameEngineNotFoundException extends RuntimeException {
    public GameEngineNotFoundException(String message) {
        super(message);
    }

    public GameEngineNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
