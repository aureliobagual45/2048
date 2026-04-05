package com.aurelio;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState {
    private final int[][] grid;

    public GameState() {
        grid = new int[4][4];
        setupGrid();
    }

    private void setupGrid() {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                grid[row][col] = 0;
            }
        }

        grid[0][0] = 2;
        grid[0][1] = 2;
    }

    public void moveUp() {
        boolean moved = false;

        for (int col = 0; col < 4; col++) {
            int[] line = new int[4];

            for (int row = 0; row < 4; row++) {
                line[row] = grid[row][col];
            }

            int[] newLine = processLine(line);

            for (int row = 0; row < 4; row++) {
                if (grid[row][col] != newLine[row]) {
                    moved = true;
                }

                grid[row][col] = newLine[row];
            }
        }

        if (moved) {
            spawnRandomTile();
        }
    }

    public void moveDown() {
        boolean moved = false;

        for (int col = 0; col < 4; col++) {
            int[] line = new int[4];

            for (int row = 0; row < 4; row++) {
                line[row] = grid[row][col];
            }

            int[] newLine = reverseLine(line);
            newLine = processLine(newLine);
            newLine = reverseLine(newLine);

            for (int row = 0; row < 4; row++) {
                if (grid[row][col] != newLine[row]) {
                    moved = true;
                }

                grid[row][col] = newLine[row];
            }
        }

        if (moved) {
            spawnRandomTile();
        }
    }

    public void moveRight() {
        boolean moved = false;

        for (int row = 0; row < 4; row++) {
            int[] line = new int[4];

            for (int col = 0; col < 4; col++) {
                line[col] = grid[row][col];
            }

            int[] newLine = reverseLine(line);
            newLine = processLine(newLine);
            newLine = reverseLine(newLine);

            for (int col = 0; col < 4; col++) {
                if (grid[row][col] != newLine[col]) {
                    moved = true;
                }

                grid[row][col] = newLine[col];
            }
        }

        if (moved) {
            spawnRandomTile();
        }
    }

    public void moveLeft() {
        boolean moved = false;

        for (int row = 0; row < 4; row++) {
            int[] line = new int[4];

            for (int col = 0; col < 4; col++) {
                line[col] = grid[row][col];
            }

            int[] newLine = processLine(line);

            for (int col = 0; col < 4; col++) {
                if (grid[row][col] != newLine[col]) {
                    moved = true;
                }

                grid[row][col] = newLine[col];
            }
        }

        if (moved) {
            spawnRandomTile();
        }
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
        grid[cell[0]][cell[1]] = random.nextDouble() < 0.9 ? 2 : 4;
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

    public int getValue(int row, int col) {
        return grid[row][col];
    }

    public void setValue(int row, int col, int value) {
        grid[row][col] = value;
    }

    public int[][] getGrid() {
        return grid;
    }
}
