package com.comp2042.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for MatrixOperations utility class.
 * Tests collision detection, boundary checking, and matrix operations.
 */
@DisplayName("MatrixOperations Tests")
class MatrixOperationsTest {

    private int[][] emptyMatrix;
    private int[][] filledMatrix;
    private int[][] brick;

    @BeforeEach
    void setUp() {
        // 10x25 board (10 rows, 25 columns) - matrix[row][col]
        emptyMatrix = new int[10][25];
        
        // Create a matrix with some filled cells
        filledMatrix = new int[10][25];
        filledMatrix[5][2] = 1;
        filledMatrix[5][3] = 1;
        filledMatrix[6][2] = 1;
        
        // Simple 2x2 brick (I-piece rotated)
        brick = new int[2][2];
        brick[0][0] = 1;
        brick[0][1] = 1;
        brick[1][0] = 1;
        brick[1][1] = 1;
    }

    @Test
    @DisplayName("Brick should not intersect with empty matrix")
    void testNoIntersectionWithEmpty() {
        boolean result = MatrixOperations.intersect(emptyMatrix, brick, 0, 0);
        assertFalse(result, "Brick should fit in empty matrix at position (0, 0)");
    }

    @Test
    @DisplayName("Brick should not go out of bounds when positioned correctly")
    void testValidPositionInBounds() {
        boolean result = MatrixOperations.intersect(emptyMatrix, brick, 5, 5);
        assertFalse(result, "Brick at (5, 5) should be valid");
    }

    @Test
    @DisplayName("Brick should intersect when placed on filled cells")
    void testIntersectionWithFilledCells() {
        // filledMatrix has cells at [5][2], [5][3], [6][2]
        // Placing brick at position (x=5, y=2):
        // i=0: targetX = 5+0 = 5, j=0: targetY = 2+0 = 2, checks brick[0][0] and matrix[2][5]
        // i=0: targetX = 5+0 = 5, j=1: targetY = 2+1 = 3, checks brick[1][0] and matrix[3][5]
        // i=1: targetX = 5+1 = 6, j=0: targetY = 2+0 = 2, checks brick[0][1] and matrix[2][6]
        // i=1: targetX = 5+1 = 6, j=1: targetY = 2+1 = 3, checks brick[1][1] and matrix[3][6]
        // None of these match the filled cells. Let me just place it directly on a filled cell
        // Actually, let's place filled cells directly where the brick will be
        filledMatrix[2][5] = 1; // This will be checked at brick position (5, 2)
        boolean result = MatrixOperations.intersect(filledMatrix, brick, 5, 2);
        assertTrue(result, "Brick should intersect with filled cells");
    }

    @Test
    @DisplayName("Copy should create independent matrix copy")
    void testMatrixCopy() {
        int[][] original = {{1, 2}, {3, 4}};
        int[][] copy = MatrixOperations.copy(original);
        
        copy[0][0] = 99;
        
        assertEquals(1, original[0][0], "Original should not be modified");
        assertEquals(99, copy[0][0], "Copy should be modified");
    }

    @Test
    @DisplayName("Copy should handle empty matrices")
    void testCopyEmptyMatrix() {
        int[][] original = new int[5][5];
        int[][] copy = MatrixOperations.copy(original);
        
        assertNotNull(copy, "Copy should not be null");
        assertEquals(original.length, copy.length, "Copy should have same dimensions");
    }

    @Test
    @DisplayName("Brick should detect out of bounds on right edge")
    void testOutOfBoundsRight() {
        // brick is 2x2, matrix is [10][25] (10 rows, 25 columns)
        // x is row, y is column, targetY goes from y to y+1
        // need targetY >= 25: y must be >= 24
        // at position (5, 24): targetY = 24+0 and 24+1=25, so 25 >= 25 is out of bounds
        boolean result = MatrixOperations.intersect(emptyMatrix, brick, 5, 24);
        assertTrue(result, "Brick at (5, 24) should go out of bounds on right");
    }

    @Test
    @DisplayName("Brick should detect out of bounds on bottom edge")
    void testOutOfBoundsBottom() {
        // brick is 2x2, matrix is [10][25] (10 rows, 25 columns)
        // y is column, targetY goes from y to y+1
        // need targetY >= 10: y must be >= 9
        // at position (4, 9): targetY = 9+0 and 9+1=10, so 10 >= 10 is out of bounds
        boolean result = MatrixOperations.intersect(emptyMatrix, brick, 4, 9);
        assertTrue(result, "Brick at (4, 9) should go out of bounds on bottom");
    }

    @Test
    @DisplayName("Brick should detect out of bounds on left edge")
    void testOutOfBoundsLeft() {
        // at position (-1, 5): targetX = -1 + 0 = -1, which is < 0
        boolean result = MatrixOperations.intersect(emptyMatrix, brick, -1, 5);
        assertTrue(result, "Brick at (-1, 5) should go out of bounds");
    }

    @Test
    @DisplayName("Brick movement at safe position should not intersect")
    void testSafePositionNoIntersection() {
        // A position that should be completely valid and in bounds
        // Position (10, 3) with 2x2 brick in [25][10] matrix
        // targetX goes from 10 to 11 - out of bounds! use 8
        // targetX goes from 8 to 9, targetY goes from 3 to 4 - all valid
        boolean result = MatrixOperations.intersect(emptyMatrix, brick, 8, 3);
        assertFalse(result, "Brick at (8, 3) should be valid and not intersect");
    }
}
