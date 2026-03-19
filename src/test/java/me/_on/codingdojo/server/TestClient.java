package me._on.codingdojo.server;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class TestClient {

    private final TestRestTemplate restTemplate;
    private final int port;

    public TestClient(TestRestTemplate restTemplate, int port) {
        this.restTemplate = restTemplate;
        this.port = port;
    }

    public Map<String, Object> createRoom(String name, String gameType) {
        Map<String, String> body = Map.of("name", name, "gameType", gameType);
        ResponseEntity<Map> response = restTemplate.postForEntity(
            "/api/rooms", body, Map.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to create room: " + response.getStatusCode());
        }
        return response.getBody();
    }

    public Map<String, Object> joinRoom(String roomCode, String playerName) {
        Map<String, String> body = Map.of("playerName", playerName);
        ResponseEntity<Map> response = restTemplate.postForEntity(
            "/api/rooms/" + roomCode + "/join", body, Map.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to join room: " + response.getStatusCode());
        }
        return response.getBody();
    }

    public Map<String, Object> getGameState(String roomCode, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
            "/api/game/" + roomCode + "/state", HttpMethod.GET, entity, Map.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get game state: " + response.getStatusCode());
        }
        return response.getBody();
    }

    public void submitMove(String roomCode, String token, String move) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.set("Content-Type", "application/json");
        Map<String, String> body = Map.of("move", move);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/game/" + roomCode + "/move", HttpMethod.POST, entity, Void.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to submit move: " + response.getStatusCode());
        }
    }

    public void startGame(String roomCode) {
        ResponseEntity<Void> response = restTemplate.postForEntity(
            "/api/admin/game/" + roomCode + "/start", null, Void.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to start game: " + response.getStatusCode());
        }
    }

    public Map<String, Object> getLeaderboard() {
        ResponseEntity<Map[]> response = restTemplate.getForEntity(
            "/api/stats/leaderboard", Map[].class);
        return Map.of("entries", response.getBody() != null ? response.getBody() : new Map[0]);
    }

    public boolean isGameRunning(String roomCode, String token) {
        try {
            Map<String, Object> state = getGameState(roomCode, token);
            String status = (String) state.get("status");
            return "IN_GAME".equals(status) || "RUNNING".equals(status);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isGameFinished(String roomCode, String token) {
        try {
            Map<String, Object> state = getGameState(roomCode, token);
            String status = (String) state.get("status");
            return "FINISHED".equals(status) || "CLOSED".equals(status);
        } catch (Exception e) {
            return false;
        }
    }
}
