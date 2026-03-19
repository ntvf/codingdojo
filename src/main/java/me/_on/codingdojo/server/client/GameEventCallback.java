package me._on.codingdojo.server.client;

@FunctionalInterface
public interface GameEventCallback {
    void onEvent(GameEvent event);
}
