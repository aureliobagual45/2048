package com.aurelio;

public class GameSessionController {
    private static final int LEADERBOARD_LIMIT = 9;

    private final GameState gameState;
    private final GameView gameView;
    private final LeaderboardStore leaderboardStore;
    private Integer pendingGameOverScore;

    public GameSessionController(GameState gameState, GameView gameView, LeaderboardStore leaderboardStore) {
        this.gameState = gameState;
        this.gameView = gameView;
        this.leaderboardStore = leaderboardStore;
    }

    public InputHandler createInputHandler() {
        return new InputHandler(gameState, gameView, this::onGameOver);
    }

    public void initialize() {
        refreshLeaderboard();
        setupCallbacks();
        gameView.render(gameState);
    }

    private void setupCallbacks() {
        gameView.setOnRestart(this::restartFromGameOver);
        gameView.setOnGameOverUndo(this::undoFromGameOver);
        gameView.setOnUndo(this::undoLastMove);
        gameView.setOnResetRequest(this::resetGame);
        gameView.setOnExitRequest(javafx.application.Platform::exit);
    }

    private void onGameOver(int score) {
        pendingGameOverScore = score;
    }

    private void restartFromGameOver() {
        if (gameView.isAnimating()) {
            return;
        }

        persistPendingGameOverScore();
        restartWithTransition();
    }

    private void undoFromGameOver() {
        if (gameView.isAnimating()) {
            return;
        }

        int[][] currentGrid = gameState.copyGrid();

        if (!gameState.undo()) {
            return;
        }

        pendingGameOverScore = null;
        gameView.hideGameOver();
        gameView.animateUndo(currentGrid, gameState, this::focusGame);
    }

    private void undoLastMove() {
        if (gameView.isAnimating()) {
            return;
        }

        int[][] currentGrid = gameState.copyGrid();

        if (!gameState.undo()) {
            return;
        }

        gameView.hideGameOver();
        gameView.animateUndo(currentGrid, gameState, this::focusGame);
    }

    private void resetGame() {
        if (gameView.isAnimating()) {
            return;
        }

        pendingGameOverScore = null;
        restartWithTransition();
    }

    private void restartWithTransition() {
        int[][] currentGrid = gameState.copyGrid();
        gameState.restart();
        gameView.hideGameOver();
        gameView.animateUndo(currentGrid, gameState, this::focusGame);
    }

    private void persistPendingGameOverScore() {
        if (pendingGameOverScore == null) {
            return;
        }

        leaderboardStore.addScore(pendingGameOverScore);
        pendingGameOverScore = null;
        refreshLeaderboard();
    }

    private void refreshLeaderboard() {
        gameView.setLeaderboardEntries(leaderboardStore.loadTop(LEADERBOARD_LIMIT));
    }

    private void focusGame() {
        gameView.getRoot().requestFocus();
    }
}