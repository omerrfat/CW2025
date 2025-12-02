package com.comp2042;

/**
 * ClearRow - Immutable data class containing line clear results.
 * 
 * Holds:
 * - Number of lines removed
 * - Updated board matrix after line removal
 * - Score bonus for the cleared lines
 * - Array of row indices that were cleared (for animation)
 * 
 * Provides safe access to data via copy to prevent external modification.
 * Used to communicate line clear results from game logic to UI.
 * 
 * @author Umer Imran
 * @version 2.0
 */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int[] clearedRows;
    private final int scoreBonus;

    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus, int[] clearedRows) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
        this.clearedRows = clearedRows == null ? new int[0] : java.util.Arrays.copyOf(clearedRows, clearedRows.length);
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }

    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    public int getScoreBonus() {
        return scoreBonus;
    }

    public int[] getClearedRows() {
        return java.util.Arrays.copyOf(clearedRows, clearedRows.length);
    }
}
