package me._on.codingdojo.server.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import me._on.codingdojo.server.AbstractIntegrationTest;
import me._on.codingdojo.server.model.Player;
import me._on.codingdojo.server.model.Room;
import me._on.codingdojo.server.model.RoomStatus;
import me._on.codingdojo.server.model.repository.PlayerRepository;
import me._on.codingdojo.server.model.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("GameController Integration Tests")
class GameControllerTest extends AbstractIntegrationTest {

    @Autowired private WebApplicationContext applicationContext;
    @Autowired private RoomRepository roomRepository;
    @Autowired private PlayerRepository playerRepository;

    private MockMvc mockMvc;
    private Room testRoom;
    private Player testPlayer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();

        testRoom = roomRepository.save(Room.builder()
                .code("TEST-ROOM").name("Test Room")
                .status(RoomStatus.RUNNING).build());

        testPlayer = playerRepository.save(Player.builder()
                .name("TestPlayer").token("valid-token")
                .roomId(testRoom.getId()).build());
    }

    @Test
    void getGameStateSuccess() throws Exception {
        mockMvc.perform(get("/api/game/TEST-ROOM/state")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getGameStateRoomNotFound() throws Exception {
        mockMvc.perform(get("/api/game/INVALID/state")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ROOM_NOT_FOUND"));
    }

    @Test
    void getGameStateGameNotStarted() throws Exception {
        mockMvc.perform(get("/api/game/TEST-ROOM/state")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("GAME_NOT_STARTED"));
    }

    @Test
    void submitMoveInvalidToken() throws Exception {
        mockMvc.perform(post("/api/game/TEST-ROOM/move")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"payload\": \"UP\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void submitMoveInvalidDirection() throws Exception {
        mockMvc.perform(post("/api/game/TEST-ROOM/move")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"payload\": \"INVALID\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void submitMoveNullDirection() throws Exception {
        mockMvc.perform(post("/api/game/TEST-ROOM/move")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"payload\": null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }
}
