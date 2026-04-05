package com.aurelio;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

public class InputHandler {
    private final GameState gameState;
    private final GameView gameView;

    public InputHandler(GameState gameState, GameView gameView) {
        this.gameState = gameState;
        this.gameView = gameView;
    }

    public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            gameState.moveUp();
        } else if (event.getCode() == KeyCode.DOWN) {
            gameState.moveDown();
        } else if (event.getCode() == KeyCode.RIGHT) {
            gameState.moveRight();
        } else if (event.getCode() == KeyCode.LEFT) {
            gameState.moveLeft();
        }

        gameView.render(gameState);
    }
}
