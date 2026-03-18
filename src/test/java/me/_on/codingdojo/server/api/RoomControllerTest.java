package me._on.codingdojo.server.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import me._on.codingdojo.server.model.Player;
import me._on.codingdojo.server.model.Room;
import me._on.codingdojo.server.model.RoomStatus;
import me._on.codingdojo.server.model.dto.CreateRoomRequest;
import me._on.codingdojo.server.model.dto.JoinRoomRequest;
import me._on.codingdojo.server.room.PlayerService;
import me._on.codingdojo.server.room.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RoomController.class)
class RoomControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomService roomService;

    @MockBean
    private PlayerService playerService;

    @Test
    void testCreateRoom() throws Exception {
        // Arrange
        CreateRoomRequest request = new CreateRoomRequest("Test Room");
        UUID roomId = UUID.randomUUID();
        Room room = Room.builder()
            .id(roomId)
            .code("ABC123")
            .name("Test Room")
            .status(RoomStatus.WAITING)
            .playerIds(List.of())
            .build();
        when(roomService.createRoom("Test Room")).thenReturn(room);

        // Act & Assert
        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value("ABC123"))
            .andExpect(jsonPath("$.name").value("Test Room"));
    }

    @Test
    void testCreateRoomInvalidRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRoomByCode() throws Exception {
        // Arrange
        UUID roomId = UUID.randomUUID();
        Room room = Room.builder()
            .id(roomId)
            .code("ABC123")
            .name("Test Room")
            .status(RoomStatus.WAITING)
            .playerIds(List.of())
            .build();
        when(roomService.findRoomByCode("ABC123")).thenReturn(Optional.of(room));

        // Act & Assert
        mockMvc.perform(get("/api/rooms/ABC123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("ABC123"))
            .andExpect(jsonPath("$.name").value("Test Room"));
    }

    @Test
    void testGetRoomByCodeNotFound() throws Exception {
        // Arrange
        when(roomService.findRoomByCode("NOTFOUND")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/rooms/NOTFOUND"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testJoinRoom() throws Exception {
        // Arrange
        JoinRoomRequest request = new JoinRoomRequest("John");
        UUID roomId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Room room = Room.builder()
            .id(roomId)
            .code("ABC123")
            .name("Test Room")
            .status(RoomStatus.WAITING)
            .playerIds(List.of(playerId))
            .build();

        Player player = Player.builder()
            .id(playerId)
            .name("John")
            .token("test-token")
            .roomId(roomId)
            .build();

        when(roomService.findRoomByCode("ABC123")).thenReturn(Optional.of(room));
        when(playerService.registerPlayer(roomId, "John")).thenReturn(player);

        // Act & Assert
        mockMvc.perform(post("/api/rooms/ABC123/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.player.token").value("test-token"))
            .andExpect(jsonPath("$.player.name").value("John"))
            .andExpect(jsonPath("$.room.code").value("ABC123"));
    }

    @Test
    void testJoinRoomNotFound() throws Exception {
        // Arrange
        JoinRoomRequest request = new JoinRoomRequest("John");
        when(roomService.findRoomByCode("NOTFOUND")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/rooms/NOTFOUND/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testJoinRoomInvalidRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/rooms/ABC123/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
