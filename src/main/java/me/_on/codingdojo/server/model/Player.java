package me._on.codingdojo.server.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {
  private UUID id;
  private String name;
  private String token;
  private UUID roomId;
}
