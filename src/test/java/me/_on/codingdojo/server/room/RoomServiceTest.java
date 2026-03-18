package me._on.codingdojo.server.room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me._on.codingdojo.server.model.Room;
import me._on.codingdojo.server.model.RoomStatus;
import me._on.codingdojo.server.model.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoomServiceTest {
    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRoom() {
        // Arrange
        String roomName = "Test Room";
        when(roomRepository.findByCode(any())).thenReturn(Optional.empty());
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Room createdRoom = roomService.createRoom(roomName);

        // Assert
        assertNotNull(createdRoom);
        assertEquals(roomName, createdRoom.getName());
        assertNotNull(createdRoom.getId());
        assertNotNull(createdRoom.getCode());
        assertEquals(6, createdRoom.getCode().length());
        assertEquals(RoomStatus.WAITING, createdRoom.getStatus());
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void testFindRoomByCode() {
        // Arrange
        UUID roomId = UUID.randomUUID();
        Room room = Room.builder()
            .id(roomId)
            .code("ABC123")
            .name("Test Room")
            .status(RoomStatus.WAITING)
            .playerIds(List.of())
            .build();
        when(roomRepository.findByCode("ABC123")).thenReturn(Optional.of(room));

        // Act
        Optional<Room> foundRoom = roomService.findRoomByCode("ABC123");

        // Assert
        assertTrue(foundRoom.isPresent());
        assertEquals("ABC123", foundRoom.get().getCode());
        verify(roomRepository).findByCode("ABC123");
    }

    @Test
    void testFindRoomByCodeNotFound() {
        // Arrange
        when(roomRepository.findByCode("NOTFOUND")).thenReturn(Optional.empty());

        // Act
        Optional<Room> foundRoom = roomService.findRoomByCode("NOTFOUND");

        // Assert
        assertTrue(foundRoom.isEmpty());
    }

    @Test
    void testFindRoomById() {
        // Arrange
        UUID roomId = UUID.randomUUID();
        Room room = Room.builder()
            .id(roomId)
            .code("ABC123")
            .name("Test Room")
            .status(RoomStatus.WAITING)
            .playerIds(List.of())
            .build();
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // Act
        Optional<Room> foundRoom = roomService.findRoomById(roomId);

        // Assert
        assertTrue(foundRoom.isPresent());
        assertEquals(roomId, foundRoom.get().getId());
    }

    @Test
    void testListRooms() {
        // Arrange
        List<Room> rooms = List.of(
            Room.builder().id(UUID.randomUUID()).code("ABC").name("Room 1")
                .status(RoomStatus.WAITING).playerIds(List.of()).build(),
            Room.builder().id(UUID.randomUUID()).code("DEF").name("Room 2")
                .status(RoomStatus.WAITING).playerIds(List.of()).build()
        );
        when(roomRepository.findAll()).thenReturn(rooms);

        // Act
        List<Room> listedRooms = roomService.listRooms();

        // Assert
        assertEquals(2, listedRooms.size());
        verify(roomRepository).findAll();
    }

    @Test
    void testDeleteRoom() {
        // Arrange
        UUID roomId = UUID.randomUUID();
        Room room = Room.builder()
            .id(roomId)
            .code("ABC123")
            .name("Test Room")
            .status(RoomStatus.WAITING)
            .playerIds(List.of())
            .build();
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // Act
        roomService.deleteRoom(roomId);

        // Assert
        verify(roomRepository).delete(room);
    }

    @Test
    void testDeleteRoomNotFound() {
        // Arrange
        UUID roomId = UUID.randomUUID();
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RoomNotFoundException.class, () -> roomService.deleteRoom(roomId));
    }
}
