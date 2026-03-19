package me._on.codingdojo.server.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class RestGameClient implements GameClient {
    
    private final String baseUrl;
    private final RestTemplate restTemplate;
    private String roomCode;
    private String token;
    private final List<GameStateCallback> stateCallbacks = new ArrayList<>();
    private final List<GameEventCallback> eventCallbacks = new ArrayList<>();
    
    public RestGameClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }
    
    @Override
    public CompletableFuture<Player> connect(String roomCode, String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = baseUrl + "/api/room/" + roomCode + "/join";
                Player response = restTemplate.postForObject(url, 
                    new JoinRoomRequest(playerName), 
                    Player.class);
                this.roomCode = roomCode;
                this.token = response.getToken();
                log.info("Connected to room: {} as {}", roomCode, playerName);
                return response;
            } catch (RestClientException e) {
                log.error("Failed to connect to room {}", roomCode, e);
                throw new RuntimeException("Connection failed", e);
            }
        });
    }
    
    @Override
    public void disconnect() {
        this.roomCode = null;
        this.token = null;
        log.info("Disconnected from game");
    }
    
    @Override
    public boolean isConnected() {
        return roomCode != null && token != null;
    }
    
    @Override
    public CompletableFuture<GameState> getGameState() {
        if (!isConnected()) {
            return CompletableFuture.failedFuture(new IllegalStateException("Not connected"));
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = baseUrl + "/api/game/" + roomCode + "/state";
                return restTemplate.getForObject(url, GameState.class);
            } catch (RestClientException e) {
                log.error("Failed to get game state", e);
                throw new RuntimeException("Failed to fetch state", e);
            }
        });
    }
    
    @Override
    public CompletableFuture<Void> submitMove(String move) {
        if (!isConnected()) {
            return CompletableFuture.failedFuture(new IllegalStateException("Not connected"));
        }
        
        return CompletableFuture.runAsync(() -> {
            try {
                String url = baseUrl + "/api/game/" + roomCode + "/move";
                MoveRequest moveRequest = new MoveRequest();
                moveRequest.setPayload(move);
                restTemplate.postForObject(url, moveRequest, Void.class);
                log.debug("Move submitted: {}", move);
            } catch (RestClientException e) {
                log.error("Failed to submit move", e);
                throw new RuntimeException("Move submission failed", e);
            }
        });
    }
    
    @Override
    public void onGameStateUpdate(GameStateCallback callback) {
        stateCallbacks.add(callback);
    }
    
    @Override
    public void onGameEvent(GameEventCallback callback) {
        eventCallbacks.add(callback);
    }
    
    protected void notifyStateUpdate(GameState state) {
        stateCallbacks.forEach(cb -> cb.onStateUpdate(state));
    }
    
    protected void notifyEvent(GameEvent event) {
        eventCallbacks.forEach(cb -> cb.onEvent(event));
    }
    
    private static class JoinRoomRequest {
        private String playerName;
        
        JoinRoomRequest(String playerName) {
            this.playerName = playerName;
        }
        
        public String getPlayerName() {
            return playerName;
        }
    }
    
    private static class MoveRequest {
        private Object payload;
        
        public Object getPayload() {
            return payload;
        }
        
        public void setPayload(Object payload) {
            this.payload = payload;
        }
    }
}
