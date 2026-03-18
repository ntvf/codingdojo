package me._on.codingdojo.server.model.repository;

import java.util.Optional;
import java.util.UUID;
import me._on.codingdojo.server.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    Optional<Room> findByCode(String code);
}
