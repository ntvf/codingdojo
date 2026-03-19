package me._on.codingdojo.server.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunnyStats {
    private String title;
    private String description;
    private List<FunnyStatEntry> entries;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FunnyStatEntry {
        private String playerName;
        private String value;
    }
}
