package me._on.codingdojo.server.engine.map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapLoader {
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    private final ResourcePatternResolver resourcePatternResolver;

    public MapConfig loadMapFromFile(String filePath) throws IOException {
        Resource resource = new ClassPathResource(filePath);
        if (!resource.exists()) {
            throw new IOException("Map file not found: " + filePath);
        }
        String yamlContent = new String(resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
        return loadMapFromYaml(yamlContent);
    }

    public MapConfig loadMapFromYaml(String yamlContent) throws IOException {
        MapConfig mapConfig = YAML_MAPPER.readValue(yamlContent, MapConfig.class);
        if (mapConfig.getCreatedAt() == 0) {
            mapConfig.setCreatedAt(System.currentTimeMillis());
        }
        return mapConfig;
    }

    public List<MapConfig> loadAllMaps(String mapDirectory) throws IOException {
        List<MapConfig> maps = new ArrayList<>();
        String pattern = "classpath:" + mapDirectory + "/**/*.yaml";
        Resource[] resources = resourcePatternResolver.getResources(pattern);

        for (Resource resource : resources) {
            try {
                String yamlContent = new String(resource.getInputStream().readAllBytes(),
                        StandardCharsets.UTF_8);
                MapConfig mapConfig = loadMapFromYaml(yamlContent);
                maps.add(mapConfig);
            } catch (Exception e) {
                throw new IOException("Failed to load map from: " + resource.getFilename(), e);
            }
        }
        return maps;
    }
}
