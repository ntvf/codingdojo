package me._on.codingdojo.server.room;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me._on.codingdojo.server.api.error.InvalidRequestException;
import me._on.codingdojo.server.model.Player;
import me._on.codingdojo.server.model.repository.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public Player registerPlayer(String playerName, UUID roomId) {
        if (playerName == null || playerName.trim().isEmpty()) {
            throw new InvalidRequestException("Player name cannot be empty");
        }

        if (roomId == null) {
            throw new InvalidRequestException("Room ID cannot be null");
        }

        String token = generatePlayerToken();
        Player player = Player.builder()
                .name(playerName.trim())
                .token(token)
                .roomId(roomId)
                .build();

        return playerRepository.save(player);
    }

    public Player validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new InvalidRequestException("Token cannot be empty");
        }

        return playerRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRequestException("Invalid token"));
    }

    public List<Player> findByRoomId(UUID roomId) {
        if (roomId == null) {
            throw new InvalidRequestException("Room ID cannot be null");
        }
        return playerRepository.findByRoomId(roomId);
    }

    private String generatePlayerToken() {
        return UUID.randomUUID().toString();
    }
}

