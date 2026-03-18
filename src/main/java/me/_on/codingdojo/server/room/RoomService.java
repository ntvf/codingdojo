package me._on.codingdojo.server.room;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me._on.codingdojo.server.api.error.InvalidRequestException;
import me._on.codingdojo.server.api.error.RoomNotFoundException;
import me._on.codingdojo.server.model.Room;
import me._on.codingdojo.server.model.RoomStatus;
import me._on.codingdojo.server.model.repository.RoomRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public Room createRoom(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidRequestException("Room name cannot be empty");
        }

        String code = generateRoomCode();
        Room room = Room.builder()
                .code(code)
                .name(name.trim())
                .status(RoomStatus.WAITING)
                .build();

        return roomRepository.save(room);
    }

    public Room findRoomByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new InvalidRequestException("Room code cannot be empty");
        }

        return roomRepository.findByCode(code)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with code: " + code));
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
