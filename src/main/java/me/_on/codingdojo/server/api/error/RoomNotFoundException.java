package me._on.codingdojo.server.api.error;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String message) {
        super(message);
    }

    public RoomNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
