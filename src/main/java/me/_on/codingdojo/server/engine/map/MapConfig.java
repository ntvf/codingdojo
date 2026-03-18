package me._on.codingdojo.server.engine.map;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * Configuration for a game map.
 * Contains dimensions, layout, and other map-specific settings.
 */
@Data
@Builder
public class MapConfig {
    private String name;
    private String type;
    private int width;
    private int height;
    private Map<String, Object> config;
    private long createdAt;
}
