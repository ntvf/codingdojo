package me._on.codingdojo.server.game.snake;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point {
    private int x;
    private int y;

    public Point move(SnakeMove direction) {
        return switch (direction) {
            case UP -> new Point(this.x, this.y - 1);
            case DOWN -> new Point(this.x, this.y + 1);
            case LEFT -> new Point(this.x - 1, this.y);
            case RIGHT -> new Point(this.x + 1, this.y);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Point point)) {
            return false;
        }
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
