package me._on.codingdojo.server.engine.map;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapConfig {
    private String name;
    private String type;
    private int width;
    private int height;
    private Map<String, Object> config;
    private long createdAt;
}
