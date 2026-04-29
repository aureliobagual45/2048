package com.aurelio;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.IntConsumer;

public class InputHandler {
    private final GameState gameState;
    private final GameView gameView;
    private final IntConsumer onGameOver;
    private final Queue<Direction> inputBuffer = new ArrayDeque<>();
    private static final int INPUT_BUFFER_LIMIT = 2;

    public InputHandler(GameState gameState, GameView gameView, IntConsumer onGameOver) {
        this.gameState = gameState;
        this.gameView = gameView;
        this.onGameOver = onGameOver;
    }

    public void handle(KeyEvent event) {
        if (gameState.isGameOver() || gameView.isConfirmationVisible()) {
            return;
        }

        Direction direction = getDirection(event.getCode());

        if (direction == null) {
            return;
        }

        if (gameView.isAnimating()) {
            bufferInput(direction);
            return;
        }

        executeMove(direction);
    }

    private Direction getDirection(KeyCode keyCode) {
        if (keyCode == KeyCode.UP) {
            return Direction.UP;
        } else if (keyCode == KeyCode.DOWN) {
            return Direction.DOWN;
        } else if (keyCode == KeyCode.RIGHT) {
            return Direction.RIGHT;
        } else if (keyCode == KeyCode.LEFT) {
            return Direction.LEFT;
        }

        return null;
    }

    private void bufferInput(Direction direction) {
        if (inputBuffer.size() >= INPUT_BUFFER_LIMIT) {
            return;
        }

        inputBuffer.add(direction);
    }

    private void executeMove(Direction direction) {
        MoveResult result = switch (direction) {
            case UP -> gameState.moveUp();
            case DOWN -> gameState.moveDown();
            case RIGHT -> gameState.moveRight();
            case LEFT -> gameState.moveLeft();
        };

        if (!result.isMoved()) {
            playNextBufferedInput();
            return;
        }

        gameView.animateMove(result, gameState, () -> {
            if (gameState.checkLose()) {
                gameState.markGameOver();
                onGameOver.accept(gameState.getScore());
                gameView.showGameOver();
                inputBuffer.clear();
                return;
            }

            playNextBufferedInput();
        });
    }

    private void playNextBufferedInput() {
        Direction nextDirection = inputBuffer.poll();

        if (nextDirection == null) {
            return;
        }

        executeMove(nextDirection);
    }
}