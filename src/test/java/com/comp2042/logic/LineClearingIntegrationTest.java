package com.comp2042.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for line clearing logic.
 * Tests detection, removal, row shifting, and scoring for line clears.
 */
@DisplayName("Line Clearing Integration Tests")
class LineClearingIntegrationTest {

    private int[][] board;

    @BeforeEach
    void setUp() {
        // 10x25 board
        board = new int[10][25];
    }

    @Test
    @DisplayName("No lines should be cleared on empty board")
    void testNoLinesClearOnEmptyBoard() {
        ClearRow result = MatrixOperations.checkRemoving(board);
        assertNotNull(result, "Result should not be null");
        assertEquals(0, result.getLinesRemoved(), "Empty board should not clear any lines");
        assertEquals(0, result.getScoreBonus(), "No score bonus for empty board");
    }

    @Test
    @DisplayName("Single line should be cleared when full")
    void testSingleLineClear() {
        // Fill row 9 completely (bottom row)
        for (int j = 0; j < 25; j++) {
            board[9][j] = 1;
        }

        ClearRow result = MatrixOperations.checkRemoving(board);
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.getLinesRemoved(), "Should clear 1 line");
        assertEquals(50, result.getScoreBonus(), "Score bonus for 1 line = 50");
    }

    @Test
    @DisplayName("Two lines should be cleared")
    void testTwoLinesClear() {
        // Fill rows 8-9 completely
        for (int j = 0; j < 25; j++) {
            board[8][j] = 1;
            board[9][j] = 1;
        }

        ClearRow result = MatrixOperations.checkRemoving(board);
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.getLinesRemoved(), "Should clear 2 lines");
        assertEquals(200, result.getScoreBonus(), "Score bonus for 2 lines = 200");
    }

    @Test
    @DisplayName("Three lines should be cleared")
    void testThreeLinesClear() {
        // Fill rows 7-9 completely
        for (int j = 0; j < 25; j++) {
            board[7][j] = 1;
            board[8][j] = 1;
            board[9][j] = 1;
        }

        ClearRow result = MatrixOperations.checkRemoving(board);
        assertNotNull(result, "Result should not be null");
        assertEquals(3, result.getLinesRemoved(), "Should clear 3 lines");
        assertEquals(450, result.getScoreBonus(), "Score bonus for 3 lines = 450");
    }

    @Test
    @DisplayName("Tetris: Four lines should be cleared")
    void testTetrisFourLinesClear() {
        // Fill rows 6-9 completely (bottom 4 rows)
        for (int j = 0; j < 25; j++) {
            board[6][j] = 1;
            board[7][j] = 1;
            board[8][j] = 1;
            board[9][j] = 1;
        }

        ClearRow result = MatrixOperations.checkRemoving(board);
        assertNotNull(result, "Result should not be null");
        assertEquals(4, result.getLinesRemoved(), "Should clear 4 lines (Tetris)");
        assertEquals(800, result.getScoreBonus(), "Score bonus for 4 lines = 800");
    }

    @Test
    @DisplayName("Rows should shift down after line clear")
    void testRowShiftingAfterClear() {
        // Set up: row 8 is full, rows 0-7 have some blocks
        board[0][0] = 1;
        board[1][5] = 1;
        board[8][0] = 1; // Mark row 8 as having a block to identify it

        // Fill row 9 completely
        for (int j = 0; j < 25; j++) {
            board[9][j] = 1;
        }

        ClearRow result = MatrixOperations.checkRemoving(board);
        int[][] newMatrix = result.getNewMatrix();

        assertNotNull(newMatrix, "New matrix should not be null");
        // After clearing row 9, row 8 should shift to row 9
        assertEquals(1, newMatrix[9][0], "Row 8 should shift down to row 9 after clear");
    }

    @Test
    @DisplayName("Top row should be empty after bottom line clear")
    void testTopRowEmptyAfterLineClear() {
        // Fill row 9 completely
        for (int j = 0; j < 25; j++) {
            board[9][j] = 1;
        }

        ClearRow result = MatrixOperations.checkRemoving(board);
        int[][] newMatrix = result.getNewMatrix();

        assertNotNull(newMatrix, "New matrix should not be null");
        // After clearing, top row should be empty
        for (int j = 0; j < 25; j++) {
            assertEquals(0, newMatrix[0][j], "Top row should be empty after line clear");
        }
    }

    @Test
    @DisplayName("Non-full lines should not be cleared")
    void testPartialLineNotCleared() {
        // Fill row 9 except for one cell
        for (int j = 0; j < 24; j++) {
            board[9][j] = 1;
        }
        // Leave board[9][24] empty

        ClearRow result = MatrixOperations.checkRemoving(board);
        assertEquals(0, result.getLinesRemoved(), "Partial line should not be cleared");
        assertEquals(0, result.getScoreBonus(), "No score bonus for partial line");
    }

    @Test
    @DisplayName("Multiple non-consecutive lines should be cleared")
    void testNonConsecutiveLinesClear() {
        // Fill rows 5 and 9 (non-consecutive)
        for (int j = 0; j < 25; j++) {
            board[5][j] = 1;
            board[9][j] = 1;
        }

        ClearRow result = MatrixOperations.checkRemoving(board);
        assertEquals(2, result.getLinesRemoved(), "Should clear both non-consecutive lines");
        assertEquals(200, result.getScoreBonus(), "Score bonus should be 200 for 2 lines");
    }

    @Test
    @DisplayName("Middle lines should be cleared correctly")
    void testMiddleLinesClear() {
        // Fill rows 3-5 (middle of board)
        for (int j = 0; j < 25; j++) {
            board[3][j] = 1;
            board[4][j] = 1;
            board[5][j] = 1;
        }

        ClearRow result = MatrixOperations.checkRemoving(board);
        assertEquals(3, result.getLinesRemoved(), "Should clear 3 middle lines");
        assertEquals(450, result.getScoreBonus(), "Score bonus for 3 lines = 450");
    }

    @Test
    @DisplayName("Returned matrix should be valid after multiple clears")
    void testReturnedMatrixValidAfterClears() {
        // Fill row 9
        for (int j = 0; j < 25; j++) {
            board[9][j] = 1;
        }

        ClearRow result = MatrixOperations.checkRemoving(board);
        int[][] newMatrix = result.getNewMatrix();

        assertNotNull(newMatrix, "Returned matrix should not be null");
        assertEquals(10, newMatrix.length, "Returned matrix should have 10 rows");
        assertEquals(25, newMatrix[0].length, "Returned matrix should have 25 columns");
    }
}
