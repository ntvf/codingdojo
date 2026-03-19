package me._on.codingdojo.server.client.example;

import me._on.codingdojo.server.client.RestGameClient;
import me._on.codingdojo.server.client.bots.BotRunner;

public class BotExample {

    private BotExample() {
        // Utility class
    }

    public static void main(String[] args) throws Exception {
        String gameServerUrl = "http://localhost:8080";
        String roomCode = "TEST-ROOM";

        RestGameClient roomCreator = new RestGameClient(gameServerUrl);
        var player = roomCreator.connect(roomCode, "Admin")
            .get();

        System.out.println("Room created: " + roomCode);
        System.out.println("Token: " + player.getToken());

        BotRunner runner = new BotRunner(gameServerUrl);
        runner.spawnBot("Bot-A", roomCode, 500L);
        runner.spawnBot("Bot-B", roomCode, 500L);

        Thread.sleep(30_000);

        runner.stopAllBots();
        roomCreator.disconnect();
        System.out.println("Game finished");
    }
}
