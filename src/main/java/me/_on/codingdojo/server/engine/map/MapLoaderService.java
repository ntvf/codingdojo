package me._on.codingdojo.server.engine.map;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapLoaderService {
    private final MapLoader mapLoader;

    public MapConfig loadMap(String mapConfigName) {
        try {
            String filePath = "maps/" + mapConfigName + ".yaml";
            return mapLoader.loadMapFromFile(filePath);
        } catch (IOException e) {
            log.error("Failed to load map: {}", mapConfigName, e);
            throw new RuntimeException("Failed to load map: " + mapConfigName, e);
        }
    }
}
