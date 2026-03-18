package me._on.codingdojo.server.api;

import lombok.RequiredArgsConstructor;
import me._on.codingdojo.server.model.Player;
import me._on.codingdojo.server.model.Room;
import me._on.codingdojo.server.model.dto.CreateRoomRequest;
import me._on.codingdojo.server.model.dto.JoinRoomRequest;
import me._on.codingdojo.server.model.dto.JoinRoomResponse;
import me._on.codingdojo.server.model.dto.PlayerResponse;
import me._on.codingdojo.server.model.dto.RoomResponse;
import me._on.codingdojo.server.room.PlayerService;
import me._on.codingdojo.server.room.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@RequestBody CreateRoomRequest request) {
        Room room = roomService.createRoom(request.getName());
        RoomResponse response = RoomResponse.builder()
                .id(room.getId())
                .code(room.getCode())
                .name(room.getName())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{code}")
    public ResponseEntity<RoomResponse> getRoom(@PathVariable String code) {
        Room room = roomService.findRoomByCode(code);
        RoomResponse response = RoomResponse.builder()
                .id(room.getId())
                .code(room.getCode())
                .name(room.getName())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{code}/join")
    public ResponseEntity<JoinRoomResponse> joinRoom(
            @PathVariable String code,
            @RequestBody JoinRoomRequest request) {
        Room room = roomService.findRoomByCode(code);
        Player player = playerService.registerPlayer(request.getPlayerName(), room.getId());

        RoomResponse roomResponse = RoomResponse.builder()
                .id(room.getId())
                .code(room.getCode())
                .name(room.getName())
                .build();

        PlayerResponse playerResponse = PlayerResponse.builder()
                .id(player.getId())
                .token(player.getToken())
                .name(player.getName())
                .build();

        JoinRoomResponse response = JoinRoomResponse.builder()
                .room(roomResponse)
                .player(playerResponse)
                .build();

        return ResponseEntity.ok(response);
    }
}
