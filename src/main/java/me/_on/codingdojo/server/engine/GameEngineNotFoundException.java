package me._on.codingdojo.server.engine;

public class GameEngineNotFoundException extends RuntimeException {
    public GameEngineNotFoundException(String message) {
        super(message);
    }

    public GameEngineNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
