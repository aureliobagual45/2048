package com.aurelio;

import java.util.List;
import java.util.Set;

public class MoveResult {
    private final boolean moved;
    private final List<TileMove> tileMoves;
    private final SpawnedTile spawnedTile;
    private final int[][] oldGrid;
    private final int[][] finalGrid;
    private final Set<GridPosition> mergedPositions;

    public MoveResult(
            boolean moved,
            List<TileMove> tileMoves,
            SpawnedTile spawnedTile,
            int[][] oldGrid,
            int[][] finalGrid,
            Set<GridPosition> mergedPositions
    ) {
        this.moved = moved;
        this.tileMoves = tileMoves;
        this.spawnedTile = spawnedTile;
        this.oldGrid = oldGrid;
        this.finalGrid = finalGrid;
        this.mergedPositions = mergedPositions;
    }

    public boolean isMoved() {
        return moved;
    }

    public List<TileMove> getTileMoves() {
        return tileMoves;
    }

    public SpawnedTile getSpawnedTile() {
        return spawnedTile;
    }

    public int[][] getOldGrid() {
        return oldGrid;
    }

    public int[][] getFinalGrid() {
        return finalGrid;
    }

    public Set<GridPosition> getMergedPositions() {
        return mergedPositions;
    }
}