package me._on.codingdojo.server.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import me._on.codingdojo.server.AbstractIntegrationTest;
import me._on.codingdojo.server.model.Room;
import me._on.codingdojo.server.model.RoomStatus;
import me._on.codingdojo.server.model.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("AdminController Integration Tests")
class AdminControllerTest extends AbstractIntegrationTest {

    @Autowired private WebApplicationContext applicationContext;
    @Autowired private RoomRepository roomRepository;

    private MockMvc mockMvc;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();

        testRoom = roomRepository.save(Room.builder()
                .code("TEST-ADMIN").name("Admin Test Room")
                .status(RoomStatus.WAITING).build());
    }

    @Test
    void startGameSuccess() throws Exception {
        mockMvc.perform(post("/api/admin/game/TEST-ADMIN/start")
                        .param("gameType", "snake")
                        .param("mapConfig", "snake-20x20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").exists());
    }

    @Test
    void startGameDefaultGameType() throws Exception {
        mockMvc.perform(post("/api/admin/game/TEST-ADMIN/start"))
                .andExpect(status().isOk());
    }

    @Test
    void startGameInvalidGameType() throws Exception {
        mockMvc.perform(post("/api/admin/game/TEST-ADMIN/start")
                        .param("gameType", "tetris"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_GAME_TYPE"));
    }

    @Test
    void startGameRoomNotFound() throws Exception {
        mockMvc.perform(post("/api/admin/game/INVALID/start"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ROOM_NOT_FOUND"));
    }

    @Test
    void stopGameSuccess() throws Exception {
        mockMvc.perform(post("/api/admin/game/TEST-ADMIN/start")
                        .param("gameType", "snake"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/admin/game/TEST-ADMIN/stop"))
                .andExpect(status().isNoContent());
    }

    @Test
    void stopGameGameNotStarted() throws Exception {
        mockMvc.perform(post("/api/admin/game/TEST-ADMIN/stop"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("GAME_NOT_STARTED"));
    }

    @Test
    void stopGameRoomNotFound() throws Exception {
        mockMvc.perform(post("/api/admin/game/INVALID/stop"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ROOM_NOT_FOUND"));
    }
}
