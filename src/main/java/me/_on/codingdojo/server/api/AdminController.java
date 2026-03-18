package me._on.codingdojo.server.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me._on.codingdojo.server.api.error.GameNotStartedException;
import me._on.codingdojo.server.api.error.InvalidGameTypeException;
import me._on.codingdojo.server.api.error.RoomNotFoundException;
import me._on.codingdojo.server.engine.GameEngineRegistry;
import me._on.codingdojo.server.engine.GameManager;
import me._on.codingdojo.server.engine.GameSession;
import me._on.codingdojo.server.engine.map.MapConfig;
import me._on.codingdojo.server.engine.map.MapLoaderService;
import me._on.codingdojo.server.model.Player;
import me._on.codingdojo.server.model.Room;
import me._on.codingdojo.server.model.RoomStatus;
import me._on.codingdojo.server.model.dto.GameStateResponse;
import me._on.codingdojo.server.room.PlayerService;
import me._on.codingdojo.server.room.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/game")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final GameManager gameManager;
    private final RoomService roomService;
    private final MapLoaderService mapLoaderService;
    private final GameEngineRegistry engineRegistry;
    private final PlayerService playerService;

    @PostMapping("/{roomCode}/start")
    public ResponseEntity<GameStateResponse> startGame(
            @PathVariable String roomCode,
            @RequestParam(defaultValue = "snake") String gameType,
            @RequestParam(defaultValue = "snake-20x20") String mapConfig) {

        Room room = roomService.findRoomByCode(roomCode);
        if (room == null) {
            throw new RoomNotFoundException("Room not found: " + roomCode);
        }

        if (!engineRegistry.hasEngine(gameType)) {
            throw new InvalidGameTypeException("Unknown game type: " + gameType);
        }

        MapConfig map = mapLoaderService.loadMap(mapConfig);
        List<Player> players = playerService.findByRoomId(room.getId());

        GameSession session = gameManager.startGame(room.getId(), gameType, map, players, room);
        room.setStatus(RoomStatus.RUNNING);
        roomService.updateRoom(room);

        log.info("Game started: room={}, type={}, players={}", roomCode, gameType, players.size());
        return ResponseEntity.ok(new GameStateResponse(session.getCurrentState()));
    }

    @PostMapping("/{roomCode}/stop")
    public ResponseEntity<Void> stopGame(@PathVariable String roomCode) {
        Room room = roomService.findRoomByCode(roomCode);
        if (room == null) {
            throw new RoomNotFoundException("Room not found: " + roomCode);
        }

        GameSession session = gameManager.getSession(room.getId())
                .orElseThrow(() -> new GameNotStartedException("Game not started"));

        gameManager.endGame(room.getId());
        room.setStatus(RoomStatus.FINISHED);
        roomService.updateRoom(room);

        log.info("Game stopped: room={}", roomCode);
        return ResponseEntity.noContent().build();
    }
}
