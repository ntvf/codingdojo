package me._on.codingdojo.server.model.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me._on.codingdojo.server.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {
    Optional<Player> findByToken(String token);

    List<Player> findByRoomId(UUID roomId);
}
