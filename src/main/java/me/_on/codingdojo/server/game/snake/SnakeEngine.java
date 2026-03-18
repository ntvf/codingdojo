package me._on.codingdojo.server.game.snake;

import lombok.extern.slf4j.Slf4j;
import me._on.codingdojo.server.engine.GameEngine;
import me._on.codingdojo.server.engine.map.MapConfig;
import me._on.codingdojo.server.model.GameState;
import me._on.codingdojo.server.model.GameStatus;
import me._on.codingdojo.server.model.Move;
import me._on.codingdojo.server.model.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
public class SnakeEngine implements GameEngine {

    private static final int MAX_TICKS_SAME_LENGTH = 200;

    @Override
    public GameState initState(Room room, MapConfig mapConfig) {
        SnakeGameState state = SnakeGameState.builder()
            .roomId(room.getId())
            .status(GameStatus.IN_PROGRESS)
            .roundNumber(1)
            .tickNumber(0)
            .playerStates(new HashMap<>())
            .boardWidth(mapConfig.getWidth())
            .boardHeight(mapConfig.getHeight())
            .direction(SnakeMove.RIGHT)
            .score(0)
            .snakeBody(initializeSnakeBody(mapConfig.getWidth(), mapConfig.getHeight()))
            .build();

        state.setFoodLocation(spawnFood(state));

        log.info("Initialized snake game on {}x{} board", mapConfig.getWidth(), mapConfig.getHeight());
        return state;
    }

    @Override
    public GameState tick(GameState currentState, Map<UUID, Move> playerMoves) {
        SnakeGameState state = (SnakeGameState) currentState;

        SnakeMove nextDirection = state.getDirection();
        if (!playerMoves.isEmpty()) {
            Move firstMove = playerMoves.values().iterator().next();
            if (firstMove instanceof SnakeMove) {
                SnakeMove moveDirection = (SnakeMove) firstMove;
                if (!moveDirection.isOpposite(state.getDirection())) {
                    nextDirection = moveDirection;
                } else {
                    log.debug("Ignored opposite move: {} (current: {})", moveDirection, state.getDirection());
                }
            }
        }
        state.setDirection(nextDirection);

        Point newHead = state.getSnakeBody().get(0).move(nextDirection);

        if (newHead.getX() < 0 || newHead.getX() >= state.getBoardWidth() ||
            newHead.getY() < 0 || newHead.getY() >= state.getBoardHeight()) {
            state.setStatus(GameStatus.FINISHED);
            log.info("Snake hit wall at {}, game over", newHead);
            return state;
        }

        for (int i = 1; i < state.getSnakeBody().size(); i++) {
            if (newHead.equals(state.getSnakeBody().get(i))) {
                state.setStatus(GameStatus.FINISHED);
                log.info("Snake self-collision at {}, game over", newHead);
                return state;
            }
        }

        List<Point> newBody = new ArrayList<>();
        newBody.add(newHead);
        newBody.addAll(state.getSnakeBody());

        if (newHead.equals(state.getFoodLocation())) {
            state.setScore(state.getScore() + 1);
            log.debug("Food eaten! Score: {}", state.getScore());
        } else {
            newBody.remove(newBody.size() - 1);
        }

        state.setSnakeBody(newBody);

        if (newHead.equals(state.getFoodLocation())) {
            state.setFoodLocation(spawnFood(state));
        }

        state.setTickNumber(state.getTickNumber() + 1);
        return state;
    }

    @Override
    public boolean isRoundOver(GameState gameState) {
        SnakeGameState state = (SnakeGameState) gameState;

        if (state.getStatus() != GameStatus.IN_PROGRESS) {
            return true;
        }

        return state.getTickNumber() >= MAX_TICKS_SAME_LENGTH && state.getScore() == 0;
    }

    private List<Point> initializeSnakeBody(int boardWidth, int boardHeight) {
        List<Point> snakeBody = new ArrayList<>();
        int centerX = boardWidth / 2;
        int centerY = boardHeight / 2;
        snakeBody.add(new Point(centerX, centerY));
        snakeBody.add(new Point(centerX - 1, centerY));
        snakeBody.add(new Point(centerX - 2, centerY));
        return snakeBody;
    }

    private Point spawnFood(SnakeGameState state) {
        Random rand = new Random();
        boolean occupied;
        Point food;

        do {
            int x = rand.nextInt(state.getBoardWidth());
            int y = rand.nextInt(state.getBoardHeight());
            food = new Point(x, y);
            final Point currentFood = food;
            occupied = state.getSnakeBody().stream().anyMatch(p -> p.equals(currentFood));
        } while (occupied);

        return food;
    }
}
