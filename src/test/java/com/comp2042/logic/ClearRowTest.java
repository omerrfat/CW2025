package com.comp2042.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for ClearRow data class.
 * Tests line clear results and scoring logic.
 */
@DisplayName("ClearRow Tests")
class ClearRowTest {

    private int[][] boardMatrix;
    private int[] clearedRowIndices;

    @BeforeEach
    void setUp() {
        // 10x25 board
        boardMatrix = new int[10][25];
        // Fill some cells to simulate cleared lines
        for (int i = 0; i < 10; i++) {
            boardMatrix[i][0] = 1;
            boardMatrix[i][1] = 2;
        }
        clearedRowIndices = new int[] { 0, 1 };
    }

    @Test
    @DisplayName("ClearRow should store lines removed count")
    void testLinesRemovedCount() {
        int linesRemoved = 2;
        int scoreBonus = 300;
        ClearRow clearRow = new ClearRow(linesRemoved, boardMatrix, scoreBonus, clearedRowIndices);

        assertEquals(2, clearRow.getLinesRemoved(), "Should store 2 lines removed");
    }

    @Test
    @DisplayName("ClearRow should calculate correct score bonus for single line")
    void testScoreBonusForSingleLine() {
        // Single line clear: 50 * 1 * 1 = 50 points
        ClearRow clearRow = new ClearRow(1, boardMatrix, 50, new int[] { 5 });

        assertEquals(50, clearRow.getScoreBonus(), "Single line clear should award 50 points");
    }

    @Test
    @DisplayName("ClearRow should calculate correct score bonus for multiple lines")
    void testScoreBonusForMultipleLines() {
        // Formula: 50 * n * n where n is lines cleared
        // 2 lines: 50 * 2 * 2 = 200
        // 3 lines: 50 * 3 * 3 = 450
        // 4 lines: 50 * 4 * 4 = 800

        ClearRow twoLines = new ClearRow(2, boardMatrix, 200, new int[] { 5, 6 });
        assertEquals(200, twoLines.getScoreBonus(), "Two line clear should award 200 points");

        ClearRow threeLines = new ClearRow(3, boardMatrix, 450, new int[] { 5, 6, 7 });
        assertEquals(450, threeLines.getScoreBonus(), "Three line clear should award 450 points");

        ClearRow fourLines = new ClearRow(4, boardMatrix, 800, new int[] { 5, 6, 7, 8 });
        assertEquals(800, fourLines.getScoreBonus(), "Four line clear (Tetris) should award 800 points");
    }

    @Test
    @DisplayName("ClearRow should return new matrix after clearing lines")
    void testNewMatrixAfterClearing() {
        ClearRow clearRow = new ClearRow(2, boardMatrix, 200, clearedRowIndices);
        int[][] newMatrix = clearRow.getNewMatrix();

        assertNotNull(newMatrix, "New matrix should not be null");
        assertEquals(10, newMatrix.length, "New matrix should have same dimensions");
        assertEquals(25, newMatrix[0].length, "New matrix should have same dimensions");
    }

    @Test
    @DisplayName("ClearRow should provide independent copy of matrix")
    void testMatrixImmutability() {
        int[][] original = new int[10][25];
        original[0][0] = 5;

        ClearRow clearRow = new ClearRow(0, original, 0, new int[] {});
        int[][] retrieved = clearRow.getNewMatrix();

        retrieved[0][0] = 99;

        assertNotEquals(99, clearRow.getNewMatrix()[0][0],
                "Modifying retrieved matrix should not affect stored matrix");
    }

    @Test
    @DisplayName("ClearRow should store cleared row indices")
    void testClearedRowIndices() {
        int[] expectedIndices = new int[] { 3, 7, 15, 20 };
        ClearRow clearRow = new ClearRow(4, boardMatrix, 800, expectedIndices);

        int[] retrieved = clearRow.getClearedRows();

        assertArrayEquals(expectedIndices, retrieved, "Should return correct cleared row indices");
    }

    @Test
    @DisplayName("ClearRow should provide independent copy of cleared rows")
    void testClearedRowsImmutability() {
        int[] original = new int[] { 3, 7 };
        ClearRow clearRow = new ClearRow(2, boardMatrix, 200, original);

        int[] retrieved = clearRow.getClearedRows();
        retrieved[0] = 99;

        assertNotEquals(99, clearRow.getClearedRows()[0],
                "Modifying retrieved indices should not affect stored indices");
    }

    @Test
    @DisplayName("ClearRow should handle null cleared rows")
    void testNullClearedRows() {
        ClearRow clearRow = new ClearRow(0, boardMatrix, 0, null);
        int[] clearedRows = clearRow.getClearedRows();

        assertNotNull(clearedRows, "Cleared rows should not be null");
        assertEquals(0, clearedRows.length, "Should have empty array for null input");
    }

    @Test
    @DisplayName("ClearRow should calculate score for no lines cleared")
    void testNoLinesClear() {
        ClearRow clearRow = new ClearRow(0, boardMatrix, 0, new int[] {});

        assertEquals(0, clearRow.getLinesRemoved(), "Should have 0 lines removed");
        assertEquals(0, clearRow.getScoreBonus(), "Should award 0 bonus points");
    }
}
