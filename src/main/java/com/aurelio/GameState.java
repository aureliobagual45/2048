package com.aurelio;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class GameState {
    private static final int HISTORY_LIMIT = 10;

    private final int[][] grid;
    private final Deque<GameSnapshot> history;
    private boolean gameOver;
    private int score;
    private long rngState;

    private static class GameSnapshot {
        int[][] grid;
        int score;
        boolean gameOver;
        long rngState;

        GameSnapshot(int[][] grid, int score, boolean gameOver, long rngState) {
            this.grid = grid;
            this.score = score;
            this.gameOver = gameOver;
            this.rngState = rngState;
        }
    }

    private static class LineTile {
        int value;
        int originalIndex;

        LineTile(int value, int originalIndex) {
            this.value = value;
            this.originalIndex = originalIndex;
        }
    }

    private static class LineResult {
        int[] newLine;
        List<TileMove> tileMoves;
        Set<GridPosition> mergedPositions;

        LineResult(int[] newLine, List<TileMove> tileMoves, Set<GridPosition> mergedPositions) {
            this.newLine = newLine;
            this.tileMoves = tileMoves;
            this.mergedPositions = mergedPositions;
        }
    }

    public GameState() {
        grid = new int[4][4];
        history = new ArrayDeque<>();
        gameOver = false;
        score = 0;
        rngState = initialSeed();
        setupGrid();
    }

    private void setupGrid() {
        rngState = initialSeed();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                grid[row][col] = 0;
            }
        }

        spawnRandomTile();
        spawnRandomTile();
    }

    private LineResult processLine(int[] line, Direction direction, int fixedIndex) {
        List<LineTile> tiles = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            if (line[i] != 0) {
                tiles.add(new LineTile(line[i], i));
            }
        }

        int[] newLine = new int[4];
        List<TileMove> tileMoves = new ArrayList<>();
        Set<GridPosition> mergedPositions = new HashSet<>();

        int writeIndex = 0;
        int i = 0;

        while (i < tiles.size()) {
            LineTile current = tiles.get(i);

            if (i + 1 < tiles.size() && current.value == tiles.get(i + 1).value) {
                LineTile next = tiles.get(i + 1);
                int mergedValue = current.value * 2;

                newLine[writeIndex] = mergedValue;

                int[] fromA = toBoardPosition(direction, fixedIndex, current.originalIndex);
                int[] fromB = toBoardPosition(direction, fixedIndex, next.originalIndex);
                int[] to = toBoardPosition(direction, fixedIndex, writeIndex);

                tileMoves.add(new TileMove(fromA[0], fromA[1], to[0], to[1], current.value, true));
                tileMoves.add(new TileMove(fromB[0], fromB[1], to[0], to[1], next.value, true));
                mergedPositions.add(new GridPosition(to[0], to[1]));

                score += mergedValue;
                i += 2;
            } else {
                newLine[writeIndex] = current.value;

                int[] from = toBoardPosition(direction, fixedIndex, current.originalIndex);
                int[] to = toBoardPosition(direction, fixedIndex, writeIndex);

                tileMoves.add(new TileMove(from[0], from[1], to[0], to[1], current.value, false));

                i += 1;
            }

            writeIndex++;
        }

        return new LineResult(newLine, tileMoves, mergedPositions);
    }

    public MoveResult moveLeft() {
        return move(Direction.LEFT);
    }

    public MoveResult moveRight() {
        return move(Direction.RIGHT);
    }

    public MoveResult moveUp() {
        return move(Direction.UP);
    }

    public MoveResult moveDown() {
        return move(Direction.DOWN);
    }

    public MoveResult move(Direction direction) {
        int[][] oldGrid = copyGrid();
        int oldScore = score;
        boolean oldGameOver = gameOver;
        long oldRngState = rngState;
        boolean moved = false;
        List<TileMove> allMoves = new ArrayList<>();
        Set<GridPosition> mergedPositions = new HashSet<>();

        for (int index = 0; index < 4; index++) {
            int[] line = getLine(direction, index);
            LineResult result = processLine(line, direction, index);

            if (writeLine(direction, index, result.newLine)) {
                moved = true;
            }

            allMoves.addAll(result.tileMoves);
            mergedPositions.addAll(result.mergedPositions);
        }

        SpawnedTile spawnedTile = null;

        if (moved) {
            history.push(new GameSnapshot(oldGrid, oldScore, oldGameOver, oldRngState));
            if (history.size() > HISTORY_LIMIT) {
                history.removeLast();
            }
            spawnedTile = spawnRandomTile();
        }

        return new MoveResult(moved, allMoves, spawnedTile, oldGrid, copyGrid(), mergedPositions);
    }

    private int[] toBoardPosition(Direction direction, int fixedIndex, int lineIndex) {
        return switch (direction) {
            case LEFT -> new int[]{fixedIndex, lineIndex};
            case RIGHT -> new int[]{fixedIndex, 3 - lineIndex};
            case UP -> new int[]{lineIndex, fixedIndex};
            case DOWN -> new int[]{3 - lineIndex, fixedIndex};
        };
    }

    private int[] getLine(Direction direction, int index) {
        return switch (direction) {
            case LEFT -> getRow(index);
            case RIGHT -> reverseLine(getRow(index));
            case UP -> getColumn(index);
            case DOWN -> reverseLine(getColumn(index));
        };
    }

    private boolean writeLine(Direction direction, int index, int[] newLine) {
        return switch (direction) {
            case LEFT -> writeRow(index, newLine);
            case RIGHT -> writeRow(index, reverseLine(newLine));
            case UP -> writeColumn(index, newLine);
            case DOWN -> writeColumn(index, reverseLine(newLine));
        };
    }

    private int[] getRow(int row) {
        return grid[row].clone();
    }

    private boolean writeRow(int row, int[] newLine) {
        boolean changed = false;

        for (int col = 0; col < 4; col++) {
            if (grid[row][col] != newLine[col]) {
                changed = true;
            }
            grid[row][col] = newLine[col];
        }

        return changed;
    }

    private int[] getColumn(int col) {
        int[] column = new int[4];

        for (int row = 0; row < 4; row++) {
            column[row] = grid[row][col];
        }

        return column;
    }

    private boolean writeColumn(int col, int[] newLine) {
        boolean changed = false;

        for (int row = 0; row < 4; row++) {
            if (grid[row][col] != newLine[row]) {
                changed = true;
            }
            grid[row][col] = newLine[row];
        }

        return changed;
    }

    private int[] reverseLine(int[] line) {
        int[] reversedLine = new int[4];

        for (int i = 0; i < line.length; i++) {
            reversedLine[i] = line[line.length - 1 - i];
        }

        return reversedLine;
    }

    private SpawnedTile spawnRandomTile() {
        List<int[]> emptyCells = new ArrayList<>();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (grid[row][col] == 0) {
                    emptyCells.add(new int[]{row, col});
                }
            }
        }

        if (emptyCells.isEmpty()) {
            return null;
        }

        int[] cell = emptyCells.get(nextInt(emptyCells.size()));
        int generatedTile = nextDouble() < 0.9 ? 2 : 4;

        grid[cell[0]][cell[1]] = generatedTile;

        return new SpawnedTile(cell[0], cell[1], generatedTile);
    }

    public boolean checkLose() {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int value = grid[row][col];

                if (value == 0)
                    return false;

                if (row < 3 && value == grid[row + 1][col])
                    return false;

                if (col < 3 && value == grid[row][col + 1])
                    return false;
            }
        }

        return true;
    }

    public int[][] copyGrid() {
        int[][] copy = new int[4][4];

        for (int row = 0; row < 4; row++) {
            System.arraycopy(grid[row], 0, copy[row], 0, 4);
        }

        return copy;
    }

    private void restoreGrid(int[][] snapshotGrid) {
        for (int row = 0; row < 4; row++) {
            System.arraycopy(snapshotGrid[row], 0, grid[row], 0, 4);
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void markGameOver() {
        gameOver = true;
    }

    public void restart() {
        history.clear();
        gameOver = false;
        score = 0;
        setupGrid();
    }

    public boolean undo() {
        if (history.isEmpty()) {
            return false;
        }

        GameSnapshot snapshot = history.pop();
        restoreGrid(snapshot.grid);
        score = snapshot.score;
        gameOver = snapshot.gameOver;
        rngState = snapshot.rngState;
        return true;
    }

    public int getValue(int row, int col) {
        return grid[row][col];
    }

    public int getScore() {
        return score;
    }

    private long initialSeed() {
        long seed = ThreadLocalRandom.current().nextLong();
        return seed != 0 ? seed : 0x9E3779B97F4A7C15L;
    }

    private long nextRandomLong() {
        long x = rngState;
        x ^= (x << 13);
        x ^= (x >>> 7);
        x ^= (x << 17);
        rngState = x;
        return x;
    }

    private int nextInt(int bound) {
        return (int) Long.remainderUnsigned(nextRandomLong(), bound);
    }

    private double nextDouble() {
        long bits = nextRandomLong() >>> 11;
        return bits * 0x1.0p-53;
    }
}
