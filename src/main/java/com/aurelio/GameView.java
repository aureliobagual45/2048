package com.aurelio;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class GameView {
    private static final int GRID_SIZE = 4;
    private static final int TILE_SIZE = 120;
    private static final int BASE_ANIMATION_MILLIS = 500;
    private static final double FRAME_BORDER_WIDTH = 10;
    private static final double FRAME_CORNER_RADIUS = 35;
    private static final String FRAME_COLOR = "#755a2d";
    private static final String BACKGROUND_COLOR = "#e6d7be";

    private final StackPane root;
    private final VBox gameLayout;
    private final VBox scoreBox;
    private final Label scoreTitleLabel;
    private final Label scoreValueLabel;
    private final BoardAnimationController boardAnimations;
    private final ControlPanel controlPanel;
    private final LeaderboardPanel leaderboardPanel;
    private final ConfirmationOverlay confirmationOverlay;
    private final GameOverOverlay gameOverOverlay;

    public GameView() {
        root = new StackPane();
        gameLayout = new VBox(20);
        scoreTitleLabel = new Label("SCORE");
        scoreValueLabel = new Label("0");
        scoreBox = new VBox(4, scoreTitleLabel, scoreValueLabel);
        boardAnimations = new BoardAnimationController(GRID_SIZE, TILE_SIZE, BASE_ANIMATION_MILLIS);
        controlPanel = new ControlPanel(BASE_ANIMATION_MILLIS);
        leaderboardPanel = new LeaderboardPanel(BASE_ANIMATION_MILLIS);
        confirmationOverlay = new ConfirmationOverlay(BASE_ANIMATION_MILLIS);
        gameOverOverlay = new GameOverOverlay(BASE_ANIMATION_MILLIS);

        setupScore();
        setupGameLayout();
        setupSidePanels();
        setupOverlays();
        setupRoot();
    }

    public StackPane getRoot() {
        return root;
    }

    public void setOnRestart(Runnable action) {
        gameOverOverlay.setOnRestart(action);
    }

    public void setOnGameOverUndo(Runnable action) {
        gameOverOverlay.setOnUndo(action);
    }

    public void setOnUndo(Runnable action) {
        controlPanel.setOnUndo(action);
    }

    public void setOnResetRequest(Runnable action) {
        controlPanel.setOnReset(() ->
                confirmationOverlay.show("Reset the game?", "RESET", action)
        );
    }

    public void setOnExitRequest(Runnable action) {
        controlPanel.setOnExit(() ->
                confirmationOverlay.show("Exit the game?", "EXIT", action)
        );
    }

    public void setLeaderboardEntries(List<LeaderboardEntry> entries) {
        leaderboardPanel.setEntries(entries);
    }

    public boolean isConfirmationVisible() {
        return confirmationOverlay.isVisible();
    }

    public boolean isAnimating() {
        return boardAnimations.isAnimating();
    }

    public void render(GameState gameState) {
        scoreValueLabel.setText(String.valueOf(gameState.getScore()));
        boardAnimations.renderGrid(gameState.copyGrid());
    }

    public void animateUndo(int[][] currentGrid, GameState gameState, Runnable onFinished) {
        scoreValueLabel.setText(String.valueOf(gameState.getScore()));
        boardAnimations.animateUndo(currentGrid, gameState.copyGrid(), onFinished);
    }

    public void animateMove(MoveResult result, GameState gameState, Runnable onFinished) {
        scoreValueLabel.setText(String.valueOf(gameState.getScore()));
        boardAnimations.animateMove(result, onFinished);
    }

    public void showGameOver() {
        gameOverOverlay.show(boardAnimations.getBoard());
    }

    public void hideGameOver() {
        gameOverOverlay.hide(boardAnimations.getBoard());
    }

    private void setupScore() {
        scoreTitleLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 60));
        scoreTitleLabel.setStyle("-fx-text-fill: #755a2d;");

        scoreValueLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 70));
        scoreValueLabel.setStyle("-fx-text-fill: #533f20;");

        scoreBox.setAlignment(Pos.CENTER);
    }

    private void setupGameLayout() {
        gameLayout.setAlignment(Pos.CENTER);
        gameLayout.getChildren().addAll(scoreBox, boardAnimations.getBoardContainer());
    }

    private void setupSidePanels() {
        StackPane.setAlignment(controlPanel, Pos.CENTER_LEFT);
        StackPane.setAlignment(leaderboardPanel, Pos.CENTER_RIGHT);
    }

    private void setupOverlays() {
        confirmationOverlay.prefWidthProperty().bind(root.widthProperty());
        confirmationOverlay.prefHeightProperty().bind(root.heightProperty());
        gameOverOverlay.prefWidthProperty().bind(root.widthProperty());
        gameOverOverlay.prefHeightProperty().bind(root.heightProperty());
    }

    private void setupRoot() {
        root.setStyle(
                "-fx-background-color: " + FRAME_COLOR + ", " + BACKGROUND_COLOR + ";" +
                        "-fx-background-insets: 0, " + FRAME_BORDER_WIDTH + ";" +
                        "-fx-background-radius: " + FRAME_CORNER_RADIUS + ", " + Math.max(0, FRAME_CORNER_RADIUS - FRAME_BORDER_WIDTH) + ";"
        );
        root.getChildren().addAll(
                gameLayout,
                controlPanel,
                leaderboardPanel,
                confirmationOverlay,
                gameOverOverlay
        );
    }
}