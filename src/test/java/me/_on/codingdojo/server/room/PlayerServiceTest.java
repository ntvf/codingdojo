package me._on.codingdojo.server.room;

import java.util.Optional;
import java.util.UUID;
import me._on.codingdojo.server.model.Player;
import me._on.codingdojo.server.model.repository.PlayerRepository;
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

class PlayerServiceTest {
    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterPlayer() {
        // Arrange
        UUID roomId = UUID.randomUUID();
        String playerName = "John";
        when(roomService.findRoomById(roomId)).thenReturn(Optional.of(null)); // Room exists
        when(playerRepository.findByToken(any())).thenReturn(Optional.empty());
        when(playerRepository.save(any(Player.class))).thenAnswer(
            invocation -> invocation.getArgument(0)
        );

        // Act
        Player registeredPlayer = playerService.registerPlayer(roomId, playerName);

        // Assert
        assertNotNull(registeredPlayer);
        assertEquals(playerName, registeredPlayer.getName());
        assertEquals(roomId, registeredPlayer.getRoomId());
        assertNotNull(registeredPlayer.getToken());
        verify(roomService).findRoomById(roomId);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void testRegisterPlayerRoomNotFound() {
        // Arrange
        UUID roomId = UUID.randomUUID();
        when(roomService.findRoomById(roomId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RoomNotFoundException.class, () ->
            playerService.registerPlayer(roomId, "John")
        );
    }

    @Test
    void testValidateToken() {
        // Arrange
        UUID playerId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        Player player = Player.builder()
            .id(playerId)
            .name("John")
            .token("valid-token")
            .roomId(roomId)
            .build();
        when(playerRepository.findByToken("valid-token")).thenReturn(Optional.of(player));

        // Act
        Player validatedPlayer = playerService.validateToken("valid-token");

        // Assert
        assertNotNull(validatedPlayer);
        assertEquals("John", validatedPlayer.getName());
    }

    @Test
    void testValidateTokenInvalid() {
        // Arrange
        when(playerRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidTokenException.class, () ->
            playerService.validateToken("invalid-token")
        );
    }

    @Test
    void testFindPlayerByToken() {
        // Arrange
        UUID playerId = UUID.randomUUID();
        Player player = Player.builder()
            .id(playerId)
            .name("John")
            .token("some-token")
            .roomId(UUID.randomUUID())
            .build();
        when(playerRepository.findByToken("some-token")).thenReturn(Optional.of(player));

        // Act
        Optional<Player> foundPlayer = playerService.findPlayerByToken("some-token");

        // Assert
        assertTrue(foundPlayer.isPresent());
        assertEquals("John", foundPlayer.get().getName());
    }

    @Test
    void testFindPlayerById() {
        // Arrange
        UUID playerId = UUID.randomUUID();
        Player player = Player.builder()
            .id(playerId)
            .name("John")
            .token("token")
            .roomId(UUID.randomUUID())
            .build();
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        // Act
        Optional<Player> foundPlayer = playerService.findPlayerById(playerId);

        // Assert
        assertTrue(foundPlayer.isPresent());
        assertEquals(playerId, foundPlayer.get().getId());
    }

    @Test
    void testLookupPlayer() {
        // Arrange
        UUID playerId = UUID.randomUUID();
        Player player = Player.builder()
            .id(playerId)
            .name("John")
            .token("token")
            .roomId(UUID.randomUUID())
            .build();
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        // Act
        Player lookedUpPlayer = playerService.lookupPlayer(playerId);

        // Assert
        assertNotNull(lookedUpPlayer);
        assertEquals(playerId, lookedUpPlayer.getId());
    }

    @Test
    void testLookupPlayerNotFound() {
        // Arrange
        UUID playerId = UUID.randomUUID();
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PlayerNotFoundException.class, () ->
            playerService.lookupPlayer(playerId)
        );
    }
}
