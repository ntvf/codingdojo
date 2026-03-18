package me._on.codingdojo.server.engine.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MapLoaderTest {
    @Autowired
    private MapLoader mapLoader;

    @Test
    void testLoadMapFromFile() throws IOException {
        MapConfig mapConfig = mapLoader.loadMapFromFile("maps/test-map.yaml");
        assertNotNull(mapConfig);
        assertEquals("Snake Arena 1", mapConfig.getName());
        assertEquals("grid", mapConfig.getType());
        assertEquals(20, mapConfig.getWidth());
        assertEquals(20, mapConfig.getHeight());
        assertNotNull(mapConfig.getConfig());
        assertEquals(4, mapConfig.getConfig().get("spawnPoints"));
    }

    @Test
    void testLoadMapFromYaml() throws IOException {
        String yaml = "name: \"Test Map\"\n"
                + "type: \"grid\"\n"
                + "width: 10\n"
                + "height: 10\n"
                + "config:\n"
                + "  spawnPoints: 2\n";

        MapConfig mapConfig = mapLoader.loadMapFromYaml(yaml);
        assertNotNull(mapConfig);
        assertEquals("Test Map", mapConfig.getName());
        assertEquals("grid", mapConfig.getType());
        assertEquals(10, mapConfig.getWidth());
        assertEquals(10, mapConfig.getHeight());
        assertEquals(2, mapConfig.getConfig().get("spawnPoints"));
    }

    @Test
    void testLoadAllMaps() throws IOException {
        List<MapConfig> maps = mapLoader.loadAllMaps("maps");
        assertNotNull(maps);
        assertEquals(1, maps.size());
        assertEquals("Snake Arena 1", maps.get(0).getName());
    }

    @Test
    void testLoadMapFromFileMissing() {
        assertThrows(IOException.class, () -> mapLoader.loadMapFromFile("maps/non-existent.yaml"));
    }
}
