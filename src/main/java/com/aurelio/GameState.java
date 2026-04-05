package com.aurelio;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState {
    private final int[][] grid;
    private boolean gameOver;
    private int score;

    public GameState() {
        grid = new int[4][4];
        gameOver = false;
        score = 0;
        setupGrid();
    }

    private void setupGrid() {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                grid[row][col] = 0;
            }
        }

        spawnRandomTile();
        spawnRandomTile();
    }

    public void moveUp() {
        boolean moved = false;

        for (int col = 0; col < 4; col++) {
            int[] newLine = processLine(getColumn(col));
            if (writeColumn(col, newLine)) {
                moved = true;
            }
        }

        if (moved) {
            spawnRandomTile();
        }
    }

    public void moveDown() {
        boolean moved = false;

        for (int col = 0; col < 4; col++) {
            int[] newLine = reverseLine(getColumn(col));
            newLine = processLine(newLine);
            newLine = reverseLine(newLine);

            if (writeColumn(col, newLine)) {
                moved = true;
            }
        }

        if (moved) {
            spawnRandomTile();
        }
    }

    public void moveRight() {
        boolean moved = false;

        for (int row = 0; row < 4; row++) {
            int[] newLine = reverseLine(getRow(row));
            newLine = processLine(newLine);
            newLine = reverseLine(newLine);

            if (writeRow(row, newLine)) {
                moved = true;
            }
        }

        if (moved) {
            spawnRandomTile();
        }
    }

    public void moveLeft() {
        boolean moved = false;

        for (int row = 0; row < 4; row++) {
            int[] newLine = processLine(getRow(row));
            if (writeRow(row, newLine)) {
                moved = true;
            }
        }

        if (moved) {
            spawnRandomTile();
        }
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

    private int[] compressLine(int[] line) {
        int[] result = new int[4];
        int writeIndex = 0;

        for (int value : line) {
            if (value != 0) {
                result[writeIndex] = value;
                writeIndex++;
            }
        }

        return result;
    }

    private int[] mergeLine(int[] line) {
        int[] result = line.clone();

        for (int i = 0; i < result.length - 1; i++) {
            if (result[i] == result[i + 1] && result[i] != 0) {
                result[i] = result[i] * 2;
                result[i + 1] = 0;
                i++;
            }
        }

        return result;
    }

    private int[] processLine(int[] line) {
        int[] processedLine = compressLine(line);
        processedLine = mergeLine(processedLine);
        return compressLine(processedLine);
    }

    private int[] reverseLine(int[] line) {
        int[] reversedLine = new int[4];

        for (int i = 0; i < line.length; i++) {
            reversedLine[i] = line[line.length - 1 - i];
        }

        return reversedLine;
    }

    private void spawnRandomTile() {
        List<int[]> emptyCells = new ArrayList<>();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (grid[row][col] == 0)
                    emptyCells.add(new int[]{row, col});
            }
        }

        if (emptyCells.isEmpty())
            return;

        Random random = new Random();
        int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));
        int generatedTile = random.nextDouble() < 0.9 ? 2 : 4;
        grid[cell[0]][cell[1]] = generatedTile;
        score += generatedTile;
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

    public boolean isGameOver() {
        return gameOver;
    }

    public void markGameOver() {
        gameOver = true;
    }

    public void restart() {
        gameOver = false;
        setupGrid();
    }

    public int getValue(int row, int col) {
        return grid[row][col];
    }

    public void setValue(int row, int col, int value) {
        grid[row][col] = value;
    }

    public int getScore() {
        return score;
    }

    public int[][] getGrid() {
        return grid;
    }
}
