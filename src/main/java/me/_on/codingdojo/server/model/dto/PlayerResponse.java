package me._on.codingdojo.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponse {
    private java.util.UUID id;
    private String token;
    private String name;
}
