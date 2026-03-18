package me._on.codingdojo.server.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me._on.codingdojo.server.api.error.GameNotStartedException;
import me._on.codingdojo.server.engine.GameManager;
import me._on.codingdojo.server.engine.GameSession;
import me._on.codingdojo.server.game.snake.SnakeMove;
import me._on.codingdojo.server.model.Move;
import me._on.codingdojo.server.model.Player;
import me._on.codingdojo.server.model.Room;
import me._on.codingdojo.server.model.dto.GameStateResponse;
import me._on.codingdojo.server.model.dto.MoveRequest;
import me._on.codingdojo.server.room.PlayerService;
import me._on.codingdojo.server.room.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameManager gameManager;
    private final RoomService roomService;
    private final PlayerService playerService;

    @GetMapping("/{roomCode}/state")
    public ResponseEntity<GameStateResponse> getGameState(@PathVariable String roomCode) {
        Room room = roomService.findRoomByCode(roomCode);

        GameSession session = gameManager.getSession(room.getId())
            .orElseThrow(() -> new GameNotStartedException("Game not started in room: " + roomCode));

        GameStateResponse response = new GameStateResponse(session.getCurrentState());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomCode}/move")
    public ResponseEntity<Void> submitMove(
            @PathVariable String roomCode,
            @RequestBody MoveRequest moveRequest,
            @RequestHeader("Authorization") String token) {

        Room room = roomService.findRoomByCode(roomCode);

        Player player = playerService.validateToken(token);

        GameSession session = gameManager.getSession(room.getId())
            .orElseThrow(() -> new GameNotStartedException("Game not started"));

        Move move = parseMove(moveRequest.getPayload());
        gameManager.submitMove(room.getId(), player.getId(), move);

        log.info("Move submitted: room={}, player={}, move={}", roomCode, player.getId(), move);
        return ResponseEntity.accepted().build();
    }

    private Move parseMove(Object direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }

        String directionStr = direction.toString().toUpperCase();
        return switch (directionStr) {
            case "UP" -> SnakeMove.UP;
            case "DOWN" -> SnakeMove.DOWN;
            case "LEFT" -> SnakeMove.LEFT;
            case "RIGHT" -> SnakeMove.RIGHT;
            default -> throw new IllegalArgumentException("Invalid direction: " + directionStr);
        };
    }
}
