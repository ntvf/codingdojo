package me._on.codingdojo.server.client.bots;

import lombok.extern.slf4j.Slf4j;
import me._on.codingdojo.server.client.GameClient;
import me._on.codingdojo.server.client.RestGameClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class BotRunner {

    private final String gameServerUrl;
    private final List<SnakeBotAI> activeBots = new ArrayList<>();

    public BotRunner(String gameServerUrl) {
        this.gameServerUrl = gameServerUrl;
    }

    public CompletableFuture<Void> spawnBot(String botName, String roomCode, long thinkTimeMs) {
        GameClient client = new RestGameClient(gameServerUrl);
        SnakeBotAI bot = new SnakeBotAI(client, botName, thinkTimeMs);
        activeBots.add(bot);

        return bot.play(roomCode)
            .exceptionally(e -> {
                log.error("Bot {} failed: {}", botName, e.getMessage());
                return null;
            })
            .thenRun(() -> activeBots.remove(bot));
    }

    public CompletableFuture<Void> spawnMultipleBots(String roomCode, int botCount, long thinkTimeMs) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < botCount; i++) {
            String botName = "Bot-" + i;
            futures.add(spawnBot(botName, roomCode, thinkTimeMs));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public void stopAllBots() {
        activeBots.forEach(SnakeBotAI::close);
        activeBots.clear();
    }
}
