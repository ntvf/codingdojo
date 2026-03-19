package me._on.codingdojo.server.model.repository;

import me._on.codingdojo.server.model.GameResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameResultRepository extends JpaRepository<GameResult, UUID> {

    List<GameResult> findByPlayerId(UUID playerId);

    List<GameResult> findByRoomId(UUID roomId);

    Page<GameResult> findAll(Pageable pageable);

    @Query("SELECT g FROM GameResult g ORDER BY g.score DESC LIMIT 100")
    List<GameResult> findTop100ByScore();

    @Query("SELECT g FROM GameResult g WHERE g.playerName = :playerName ORDER BY g.score DESC")
    List<GameResult> findByPlayerName(String playerName);
}
