package me._on.codingdojo.server.client;

@FunctionalInterface
public interface GameStateCallback {
    void onStateUpdate(GameState state);
}
