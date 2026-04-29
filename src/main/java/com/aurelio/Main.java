package com.aurelio;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {
    private static final double BASE_WIDTH = 1000;
    private static final double BASE_HEIGHT = 1000;
    private static final double SCREEN_USAGE_RATIO = 0.9;
    private static final String FRAME_COLOR = "#755a2d";

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        GameState gameState = new GameState();
        LeaderboardStore leaderboardStore = new LeaderboardStore();
        GameView gameView = new GameView();
        GameSessionController sessionController = new GameSessionController(gameState, gameView, leaderboardStore);
        InputHandler inputHandler = sessionController.createInputHandler();

        sessionController.initialize();

        StackPane scalableRoot = gameView.getRoot();
        scalableRoot.setMinSize(BASE_WIDTH, BASE_HEIGHT);
        scalableRoot.setPrefSize(BASE_WIDTH, BASE_HEIGHT);
        scalableRoot.setMaxSize(BASE_WIDTH, BASE_HEIGHT);

        StackPane viewport = new StackPane(scalableRoot);
        viewport.setStyle("-fx-background-color: " + FRAME_COLOR + ";");

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double initialScale = Math.min(
                1.0,
                Math.min(
                        (bounds.getWidth() * SCREEN_USAGE_RATIO) / BASE_WIDTH,
                        (bounds.getHeight() * SCREEN_USAGE_RATIO) / BASE_HEIGHT
                )
        );

        double initialWidth = BASE_WIDTH * initialScale;
        double initialHeight = BASE_HEIGHT * initialScale;

        Scene scene = new Scene(viewport, initialWidth, initialHeight, Color.web(FRAME_COLOR));

        applyScale(scalableRoot, initialWidth, initialHeight);

        scene.widthProperty().addListener((obs, oldValue, newValue) ->
                applyScale(scalableRoot, newValue.doubleValue(), scene.getHeight())
        );
        scene.heightProperty().addListener((obs, oldValue, newValue) ->
                applyScale(scalableRoot, scene.getWidth(), newValue.doubleValue())
        );

        scene.setOnKeyPressed(inputHandler::handle);

        InputStream iconStream = getClass().getResourceAsStream("/images/icon.png");

        if (iconStream != null) {
            Image icon = new Image(iconStream);
            stage.getIcons().add(icon);
        }
        stage.setTitle("2048");
        stage.setScene(scene);
        stage.show();
        gameView.getRoot().requestFocus();
    }

    private void applyScale(StackPane content, double availableWidth, double availableHeight) {
        double scale = Math.min(
                availableWidth / BASE_WIDTH,
                availableHeight / BASE_HEIGHT
        );

        content.setScaleX(scale);
        content.setScaleY(scale);
    }
}