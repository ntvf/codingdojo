package me._on.codingdojo.server.web;

import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me._on.codingdojo.server.model.GameState;
import me._on.codingdojo.server.model.dto.GameStateMessage;
import me._on.codingdojo.server.model.dto.GameStartedMessage;
import me._on.codingdojo.server.model.dto.GameEndedMessage;
import me._on.codingdojo.server.model.dto.PlayerJoinedMessage;
import me._on.codingdojo.server.model.dto.PlayerLeftMessage;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastGameState(UUID roomId, GameState gameState) {
        String topic = "/topic/game/" + roomId;
        GameStateMessage message = GameStateMessage.builder()
            .state(gameState)
            .build();
        messagingTemplate.convertAndSend(topic, message);
        log.debug("Broadcasted game state: room={}, state={}", roomId, gameState.getClass().getSimpleName());
    }

    public void broadcastGameStarted(UUID roomId, GameState gameState) {
        String topic = "/topic/game/" + roomId;
        GameStartedMessage message = GameStartedMessage.builder()
            .state(gameState)
            .build();
        messagingTemplate.convertAndSend(topic, message);
        log.info("Game started broadcast: room={}", roomId);
    }

    public void broadcastGameEnded(UUID roomId, GameState gameState) {
        String topic = "/topic/game/" + roomId;
        GameEndedMessage message = GameEndedMessage.builder()
            .state(gameState)
            .build();
        messagingTemplate.convertAndSend(topic, message);
        log.info("Game ended broadcast: room={}", roomId);
    }

    public void broadcastPlayerJoined(UUID roomId, UUID playerId, String playerName) {
        String topic = "/topic/room/" + roomId;
        PlayerJoinedMessage message = PlayerJoinedMessage.builder()
            .playerId(playerId)
            .playerName(playerName)
            .build();
        messagingTemplate.convertAndSend(topic, message);
        log.debug("Player joined broadcast: room={}, player={}", roomId, playerId);
    }

    public void broadcastPlayerLeft(UUID roomId, UUID playerId) {
        String topic = "/topic/room/" + roomId;
        PlayerLeftMessage message = PlayerLeftMessage.builder()
            .playerId(playerId)
            .build();
        messagingTemplate.convertAndSend(topic, message);
        log.debug("Player left broadcast: room={}, player={}", roomId, playerId);
    }
}
