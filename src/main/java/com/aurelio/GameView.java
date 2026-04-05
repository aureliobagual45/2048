package com.aurelio;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class GameView {
    private static final int GRID_SIZE = 4;
    private static final int TILE_SIZE = 120;
    private static final int MILLISECONDS = 500;

    private final StackPane root;
    private final StackPane overlay;
    private final GridPane board;
    private final Button restartButton;
    private final Label scoreTitleLabel;
    private final Label scoreValueLabel;
    private final VBox scoreBox;
    private final VBox gameLayout;
    private final Label gameOverLabel;
    private final VBox gameOverContent;
    private final GaussianBlur gameOverBlur;

    public GameView() {
        root = new StackPane();
        board = new GridPane();
        overlay = new StackPane();

        scoreTitleLabel = new Label("SCORE");
        scoreValueLabel = new Label("0");
        scoreBox = new VBox(4, scoreTitleLabel, scoreValueLabel);
        gameLayout = new VBox(20, scoreBox, board);

        restartButton = new Button("RESTART");
        gameOverLabel = new Label("GAME OVER");
        gameOverContent = new VBox(40);
        gameOverContent.getChildren().addAll(gameOverLabel, restartButton);
        gameOverBlur = new GaussianBlur(0);

        setupLayout();
        setupOverlay();
        setupScoreLabels();
        setupScoreBox();
        setupGameLayout();
        setupGameOverLabel();
        setupRestartButton();
        setupGameOverContent();
    }

    public StackPane getRoot() {
        return root;
    }

    /// SETUP

    private void setupLayout() {
        board.setAlignment(Pos.CENTER);
        board.setFocusTraversable(true);
        board.setHgap(10);
        board.setVgap(10);

        for (int i = 0; i < GRID_SIZE; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setMinWidth(TILE_SIZE);
            col.setPrefWidth(TILE_SIZE);
            col.setMaxWidth(TILE_SIZE);
            board.getColumnConstraints().add(col);

            RowConstraints row = new RowConstraints();
            row.setMinHeight(TILE_SIZE);
            row.setPrefHeight(TILE_SIZE);
            row.setMaxHeight(TILE_SIZE);
            board.getRowConstraints().add(row);
        }

        root.setStyle("-fx-background-color: #e6d7be;");

        root.getChildren().add(gameLayout);
        root.getChildren().add(overlay);
    }

    private void setupScoreLabels() {
        scoreTitleLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 60));
        scoreTitleLabel.setStyle("-fx-text-fill: #755a2d;");

        scoreValueLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 70));
        scoreValueLabel.setStyle("-fx-text-fill: #533f20;");
    }

    private void setupScoreBox() {
        scoreBox.setAlignment(Pos.CENTER);

    }private void setupGameLayout() {
        gameLayout.setAlignment(Pos.CENTER);
    }

    private void setupOverlay() {
        overlay.prefWidthProperty().bind(root.widthProperty());
        overlay.prefHeightProperty().bind(root.heightProperty());
        overlay.setStyle("-fx-background-color: rgba(230, 215, 190, 0.5);");
        overlay.setVisible(false);
        overlay.setOpacity(0);

        gameOverContent.setAlignment(Pos.CENTER);
    }

    private void setupGameOverLabel() {
        gameOverLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 10));
        gameOverLabel.setStyle("-fx-text-fill: #533f20;");
    }

    private void setupRestartButton() {
        String normalStyle =
                "-fx-background-color: rgba(117, 90, 45, 0.0);" +
                        "-fx-text-fill: #533f20;" +
                        "-fx-background-radius: 999;" +
                        "-fx-border-radius: 999;" +
                        "-fx-padding: 12 32 12 32;";

        String hoverStyle =
                "-fx-background-color: rgba(117, 90, 45, 0.25);" +
                        "-fx-text-fill: #533f20;" +
                        "-fx-background-radius: 999;" +
                        "-fx-border-radius: 999;" +
                        "-fx-padding: 12 32 12 32;";

        restartButton.setFont(Font.font("Consolas", FontWeight.BOLD, 40));
        restartButton.setStyle(normalStyle);

        restartButton.setOnMouseEntered(event -> restartButton.setStyle(hoverStyle));
        restartButton.setOnMouseExited(event -> restartButton.setStyle(normalStyle));
    }

    private void setupGameOverContent() {
        gameOverContent.setAlignment(Pos.CENTER);
        overlay.getChildren().add(gameOverContent);
    }

    /// RENDER

    public void render(GameState gameState) {
        board.getChildren().clear();

        scoreValueLabel.setText(String.valueOf(gameState.getScore()));

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                StackPane tile = createTile(gameState.getValue(row, col));
                board.add(tile, col, row);
            }
        }
    }

    /// TILES

    private StackPane createTile(int value) {
        StackPane tile = new StackPane();
        tile.setPrefSize(TILE_SIZE, TILE_SIZE);
        tile.setMinSize(TILE_SIZE, TILE_SIZE);
        tile.setMaxSize(TILE_SIZE, TILE_SIZE);
        tile.setAlignment(Pos.CENTER);

        Color baseColor = getTileColorObject(value);

        tile.setStyle(
                "-fx-background-color: " + toHex(baseColor) + ";" +
                        "-fx-background-radius: 10;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setRadius(4);
        shadow.setOffsetX(4);
        shadow.setOffsetY(4);
        shadow.setColor(baseColor.darker());
        tile.setEffect(shadow);

        if (value != 0) {
            Label label = new Label(String.valueOf(value));
            label.setFont(Font.font("Consolas", FontWeight.BOLD, getFontSize(value)));
            label.setStyle("-fx-text-fill: white;");
            label.setTranslateX(-2);
            label.setTranslateY(-2);
            tile.getChildren().add(label);
        }

        return tile;
    }

    private Color getTileColorObject(int value) {
        if (value == 0) {
            return Color.web("#d5cdc4");
        }

        int level = getLevel(value);
        double t = Math.min(level / 11.0, 1.0);

        double startHue = 40;
        double endHue = 275;
        double delta = ((endHue - startHue + 360) % 360) - 360;
        double hue = (startHue + delta * t + 360) % 360;
        double saturation = 0.75;
        double brightness = 0.95 - 0.35 * t;

        return Color.hsb(hue, saturation, brightness);
    }

    private double getFontSize(int value) {
        if (value == 0) {
            return 0;
        }

        int digits = String.valueOf(value).length();
        double size = 80 - digits * 8;
        return Math.max(size, 16);
    }

    private String toHex(Color color) {
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);

        return String.format("#%02x%02x%02x", r, g, b);
    }

    /// GAME OVER

    public void showGameOver() {
        overlay.setVisible(true);
        overlay.setOpacity(0);

        gameOverLabel.setOpacity(1);
        gameOverLabel.setScaleX(150);
        gameOverLabel.setScaleY(150);

        restartButton.setOpacity(0);
        restartButton.setScaleX(0);
        restartButton.setScaleY(0);

        board.setEffect(gameOverBlur);

        Timeline blurAnimation = new Timeline(
                new KeyFrame(Duration.millis(MILLISECONDS),
                        new KeyValue(gameOverBlur.radiusProperty(), 12))
        );

        FadeTransition fadeGameOver = new FadeTransition(Duration.millis(MILLISECONDS), overlay);
        fadeGameOver.setFromValue(0);
        fadeGameOver.setToValue(1);

        ScaleTransition scaleGameOver = new ScaleTransition(Duration.millis(MILLISECONDS), gameOverLabel);
        scaleGameOver.setFromX(150);
        scaleGameOver.setFromY(150);
        scaleGameOver.setToX(10);
        scaleGameOver.setToY(10);
        scaleGameOver.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fadeRestart = new FadeTransition(Duration.millis(MILLISECONDS), restartButton);
        fadeRestart.setFromValue(0);
        fadeRestart.setToValue(1);

        ScaleTransition scaleRestart = new ScaleTransition(Duration.millis(MILLISECONDS), restartButton);
        scaleRestart.setFromX(0);
        scaleRestart.setFromY(0);
        scaleRestart.setToX(1);
        scaleRestart.setToY(1);
        scaleRestart.setInterpolator(Interpolator.EASE_OUT);

        scaleGameOver.setOnFinished(event -> {
            fadeRestart.play();
            scaleRestart.play();
        });

        blurAnimation.play();
        fadeGameOver.play();
        scaleGameOver.play();
    }

    public void hideGameOver() {
        FadeTransition fadeOutOverlay = new FadeTransition(Duration.millis(MILLISECONDS), overlay);
        fadeOutOverlay.setFromValue(overlay.getOpacity());
        fadeOutOverlay.setToValue(0);

        FadeTransition fadeOutLabel = new FadeTransition(Duration.millis(MILLISECONDS), gameOverLabel);
        fadeOutLabel.setFromValue(gameOverLabel.getOpacity());
        fadeOutLabel.setToValue(0);

        FadeTransition fadeOutRestart = new FadeTransition(Duration.millis(MILLISECONDS), restartButton);
        fadeOutRestart.setFromValue(restartButton.getOpacity());
        fadeOutRestart.setToValue(0);

        if (board.getEffect() instanceof GaussianBlur blur) {
            Timeline blurOut = new Timeline(
                    new KeyFrame(Duration.millis(MILLISECONDS),
                            new KeyValue(blur.radiusProperty(), 0))
            );

            blurOut.setOnFinished(event -> board.setEffect(null));
            blurOut.play();
        }

        fadeOutOverlay.setOnFinished(event -> {
            overlay.setVisible(false);
            overlay.setOpacity(0);

            gameOverLabel.setOpacity(1);
            gameOverLabel.setScaleX(1);
            gameOverLabel.setScaleY(1);

            restartButton.setOpacity(1);
            restartButton.setScaleX(1);
            restartButton.setScaleY(1);

            board.setEffect(null);
        });

        fadeOutOverlay.play();
        fadeOutLabel.play();
        fadeOutRestart.play();
    }

    public void setOnRestart(Runnable action) {
        restartButton.setOnAction(event -> action.run());
    }

    /// HELPERS

    private int getLevel(int value) {
        return (int) (Math.log(value) / Math.log(2));
    }

    public void setScore(int score) {
        scoreValueLabel.setText(String.valueOf(score));
    }
}
