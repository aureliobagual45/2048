package com.aurelio;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Set;

public class BoardAnimationController {
    private final int gridSize;
    private final int tileSize;
    private final int baseAnimationMillis;
    private final TileFactory tileFactory;
    private final GridPane board;
    private final Pane tileLayer;
    private final StackPane boardContainer;
    private boolean animating;

    public BoardAnimationController(int gridSize, int tileSize, int baseAnimationMillis) {
        this.gridSize = gridSize;
        this.tileSize = tileSize;
        this.baseAnimationMillis = baseAnimationMillis;
        this.tileFactory = new TileFactory(tileSize);
        this.board = new GridPane();
        this.tileLayer = new Pane();
        this.boardContainer = new StackPane();
        this.animating = false;

        setupBoard();
        setupBoardContainer();
    }

    public GridPane getBoard() {
        return board;
    }

    public StackPane getBoardContainer() {
        return boardContainer;
    }

    public boolean isAnimating() {
        return animating;
    }

    public void renderGrid(int[][] grid) {
        board.getChildren().clear();

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                board.add(tileFactory.createTile(grid[row][col]), col, row);
            }
        }
    }

    public void renderGridWithHiddenPositions(int[][] grid, Set<GridPosition> hiddenPositions) {
        board.getChildren().clear();

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                GridPosition position = new GridPosition(row, col);
                int value = hiddenPositions.contains(position) ? 0 : grid[row][col];
                board.add(tileFactory.createTile(value), col, row);
            }
        }
    }

    public void animateUndo(int[][] currentGrid, int[][] restoredGrid, Runnable onFinished) {
        animating = true;
        tileLayer.getChildren().clear();
        renderGrid(restoredGrid);

        GridPane currentBoardOverlay = createBoardOverlay(currentGrid);
        boardContainer.getChildren().add(currentBoardOverlay);

        FadeTransition fadeOut = new FadeTransition(animationDuration(0.4), currentBoardOverlay);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setInterpolator(Interpolator.EASE_OUT);
        fadeOut.setOnFinished(event -> {
            boardContainer.getChildren().remove(currentBoardOverlay);
            animating = false;
            onFinished.run();
        });
        fadeOut.play();
    }

    public void animateMove(MoveResult result, Runnable onFinished) {
        animating = true;
        tileLayer.getChildren().clear();

        renderGrid(createSlideGrid(result));

        ParallelTransition slideAnimation = new ParallelTransition();

        for (TileMove move : result.getTileMoves()) {
            boolean actuallyMoved =
                    move.getFromRow() != move.getToRow() ||
                            move.getFromCol() != move.getToCol();

            if (!actuallyMoved && !move.isMerged()) {
                continue;
            }

            StackPane tile = tileFactory.createFloatingTile(
                    move.getValue(),
                    move.getFromRow(),
                    move.getFromCol(),
                    getTileX(move.getFromCol()),
                    getTileY(move.getFromRow())
            );
            tileLayer.getChildren().add(tile);

            double deltaX = getTileX(move.getToCol()) - getTileX(move.getFromCol());
            double deltaY = getTileY(move.getToRow()) - getTileY(move.getFromRow());

            TranslateTransition transition = new TranslateTransition(animationDuration(0.24), tile);
            transition.setByX(deltaX);
            transition.setByY(deltaY);
            transition.setInterpolator(Interpolator.EASE_OUT);

            slideAnimation.getChildren().add(transition);
        }

        Runnable finishSequence = () -> {
            tileLayer.getChildren().clear();

            Set<GridPosition> hiddenAfterSlide = new HashSet<>(result.getMergedPositions());
            SpawnedTile spawnedTile = result.getSpawnedTile();

            if (spawnedTile != null) {
                hiddenAfterSlide.add(new GridPosition(spawnedTile.getRow(), spawnedTile.getCol()));
            }

            renderGridWithHiddenPositions(result.getFinalGrid(), hiddenAfterSlide);

            playMergeAndSpawnPop(result, spawnedTile, () -> {
                tileLayer.getChildren().clear();
                renderGrid(result.getFinalGrid());
                animating = false;
                onFinished.run();
            });
        };

        if (slideAnimation.getChildren().isEmpty()) {
            finishSequence.run();
            return;
        }

        slideAnimation.setOnFinished(event -> finishSequence.run());
        slideAnimation.play();
    }

    private void setupBoard() {
        configureBoardGrid(board);
        board.setFocusTraversable(true);
    }

    private void setupBoardContainer() {
        double boardSize = gridSize * tileSize + (gridSize - 1) * board.getHgap();

        tileLayer.setPickOnBounds(false);
        tileLayer.setMouseTransparent(true);
        tileLayer.setMinSize(boardSize, boardSize);
        tileLayer.setPrefSize(boardSize, boardSize);
        tileLayer.setMaxSize(boardSize, boardSize);

        boardContainer.setMinSize(boardSize, boardSize);
        boardContainer.setPrefSize(boardSize, boardSize);
        boardContainer.setMaxSize(boardSize, boardSize);
        boardContainer.getChildren().addAll(board, tileLayer);
    }

    private GridPane createBoardOverlay(int[][] grid) {
        GridPane overlayBoard = new GridPane();
        configureBoardGrid(overlayBoard);
        overlayBoard.setMouseTransparent(true);
        overlayBoard.setPickOnBounds(false);
        overlayBoard.setMinSize(boardContainer.getPrefWidth(), boardContainer.getPrefHeight());
        overlayBoard.setPrefSize(boardContainer.getPrefWidth(), boardContainer.getPrefHeight());
        overlayBoard.setMaxSize(boardContainer.getPrefWidth(), boardContainer.getPrefHeight());
        StackPane.setAlignment(overlayBoard, Pos.CENTER);

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                overlayBoard.add(tileFactory.createTile(grid[row][col]), col, row);
            }
        }

        return overlayBoard;
    }

    private int[][] createSlideGrid(MoveResult result) {
        int[][] animationGrid = new int[gridSize][gridSize];
        int[][] oldGrid = result.getOldGrid();

        for (int row = 0; row < gridSize; row++) {
            System.arraycopy(oldGrid[row], 0, animationGrid[row], 0, gridSize);
        }

        for (TileMove move : result.getTileMoves()) {
            boolean actuallyMoved =
                    move.getFromRow() != move.getToRow() ||
                            move.getFromCol() != move.getToCol();

            if (actuallyMoved || move.isMerged()) {
                animationGrid[move.getFromRow()][move.getFromCol()] = 0;
            }
        }

        for (GridPosition position : result.getMergedPositions()) {
            animationGrid[position.getRow()][position.getCol()] = 0;
        }

        SpawnedTile spawnedTile = result.getSpawnedTile();
        if (spawnedTile != null) {
            animationGrid[spawnedTile.getRow()][spawnedTile.getCol()] = 0;
        }

        return animationGrid;
    }

    private void playMergeAndSpawnPop(MoveResult result, SpawnedTile spawnedTile, Runnable onFinished) {
        ParallelTransition popAnimation = new ParallelTransition();
        Set<GridPosition> used = new HashSet<>();

        for (TileMove move : result.getTileMoves()) {
            if (!move.isMerged()) {
                continue;
            }

            GridPosition position = new GridPosition(move.getToRow(), move.getToCol());
            if (!used.add(position)) {
                continue;
            }

            int mergedValue = move.getValue() * 2;
            StackPane tile = tileFactory.createFloatingTile(
                    mergedValue,
                    move.getToRow(),
                    move.getToCol(),
                    getTileX(move.getToCol()),
                    getTileY(move.getToRow())
            );
            tileLayer.getChildren().add(tile);

            ScaleTransition scaleUp = new ScaleTransition(animationDuration(0.11), tile);
            scaleUp.setToX(1.10);
            scaleUp.setToY(1.10);
            scaleUp.setInterpolator(Interpolator.EASE_OUT);

            ScaleTransition scaleDown = new ScaleTransition(animationDuration(0.14), tile);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.setInterpolator(Interpolator.EASE_IN);

            popAnimation.getChildren().add(new SequentialTransition(scaleUp, scaleDown));
        }

        if (spawnedTile != null) {
            StackPane tile = tileFactory.createFloatingTile(
                    spawnedTile.getValue(),
                    spawnedTile.getRow(),
                    spawnedTile.getCol(),
                    getTileX(spawnedTile.getCol()),
                    getTileY(spawnedTile.getRow())
            );
            tile.setScaleX(0.0);
            tile.setScaleY(0.0);
            tileLayer.getChildren().add(tile);

            ScaleTransition scaleIn = new ScaleTransition(animationDuration(0.20), tile);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);
            scaleIn.setInterpolator(Interpolator.EASE_OUT);
            popAnimation.getChildren().add(scaleIn);
        }

        if (popAnimation.getChildren().isEmpty()) {
            onFinished.run();
            return;
        }

        popAnimation.setOnFinished(event -> onFinished.run());
        popAnimation.play();
    }

    private double getTileX(int col) {
        return col * (tileSize + board.getHgap());
    }

    private double getTileY(int row) {
        return row * (tileSize + board.getVgap());
    }

    private Duration animationDuration(double multiplier) {
        return Duration.millis(baseAnimationMillis * multiplier);
    }

    private void configureBoardGrid(GridPane gridPane) {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        for (int i = 0; i < gridSize; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setMinWidth(tileSize);
            col.setPrefWidth(tileSize);
            col.setMaxWidth(tileSize);
            gridPane.getColumnConstraints().add(col);

            RowConstraints row = new RowConstraints();
            row.setMinHeight(tileSize);
            row.setPrefHeight(tileSize);
            row.setMaxHeight(tileSize);
            gridPane.getRowConstraints().add(row);
        }
    }
}
