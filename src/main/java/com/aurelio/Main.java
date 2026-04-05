package com.aurelio;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        GameState gameState = new GameState();
        GameView gameView = new GameView();
        InputHandler inputHandler = new InputHandler(gameState, gameView);

        gameView.setOnRestart(() -> {
            gameState.restart();
            gameView.hideGameOver();
            gameView.render(gameState);
            gameView.getRoot().requestFocus();
        });

        gameView.render(gameState);

        Scene scene = new Scene(gameView.getRoot(), 1000, 1000, Color.web("#e6d7be"));

        scene.setOnKeyPressed(inputHandler::handle);

        Image icon = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/images/icon.png"))
        );
        stage.getIcons().add(icon);
        stage.setTitle("2048");
        ///stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        gameView.getRoot().requestFocus();
    }
}
