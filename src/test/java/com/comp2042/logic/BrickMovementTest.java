package com.comp2042.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for brick movement and collision detection.
 * Tests movement boundaries and collision handling.
 */
@DisplayName("Brick Movement and Collision Tests")
class BrickMovementTest {

    private int[][] board;
    private int[][] brick;

    @BeforeEach
    void setUp() {
        // 10x25 board
        board = new int[10][25];
        // O-piece: 2x2 square in center of 4x4 array
        brick = new int[4][4];
        brick[1][1] = 1;
        brick[1][2] = 1;
        brick[2][1] = 1;
        brick[2][2] = 1;
    }

    @Test
    @DisplayName("Brick should move left within bounds")
    void testBrickMovementLeftValid() {
        // Place brick at position (2, 2) - middle of board
        boolean canMove = !MatrixOperations.intersect(board, brick, 2, 2);
        assertTrue(canMove, "Brick should be able to move in valid position");
    }

    @Test
    @DisplayName("Brick should move within bounds")
    void testBrickMovementRightValid() {
        // Place brick at position (0, 2) - still valid
        boolean canMove = !MatrixOperations.intersect(board, brick, 0, 2);
        assertTrue(canMove, "Brick should be able to move within bounds");
    }

    @Test
    @DisplayName("Brick should move down within bounds")
    void testBrickMovementDownValid() {
        // Place brick at position (4, 5) - approaching bottom, still valid
        boolean canMove = !MatrixOperations.intersect(board, brick, 4, 5);
        assertTrue(canMove, "Brick should be able to move down from valid position");
    }

    @Test
    @DisplayName("Brick should be blocked at right boundary")
    void testBrickBlockedAtRightBoundary() {
        // Try to place brick at position where it goes past column 24 (out of bounds)
        boolean blocked = MatrixOperations.intersect(board, brick, 0, 22);
        assertTrue(blocked, "Brick should be blocked at right boundary");
    }

    @Test
    @DisplayName("Brick should detect collision with existing blocks")
    void testBrickCollisionWithBlocks() {
        // Fill cells in the board where brick would land
        board[3][12] = 1;
        board[3][13] = 1;

        // Try to place brick at (2, 10) - will collide with filled cells at [3][12-13]
        boolean collision = MatrixOperations.intersect(board, brick, 2, 10);
        assertTrue(collision, "Brick should detect collision with existing blocks");
    }

    @Test
    @DisplayName("Brick should not collide when gap exists")
    void testNoBrickCollisionWithGap() {
        // Fill row 8 completely
        for (int j = 0; j < 25; j++) {
            board[8][j] = 1;
        }

        // Try to place brick at (3, 5) - well above the filled row
        boolean collision = MatrixOperations.intersect(board, brick, 3, 5);
        assertFalse(collision, "Brick should fit in empty space");
    }

    @Test
    @DisplayName("Brick should stop at filled row")
    void testBrickStopAtFilledRow() {
        // Fill rows 6-7 completely
        for (int j = 0; j < 25; j++) {
            board[6][j] = 1;
            board[7][j] = 1;
        }

        // Brick at (5, 5) would collide with filled rows 6-7
        boolean collision = MatrixOperations.intersect(board, brick, 5, 5);
        assertTrue(collision, "Brick should collide with filled row");
    }

    @Test
    @DisplayName("I-piece should fit through open space")
    void testLongBrickFitsSpace() {
        // I-piece (vertical orientation in 4x4)
        int[][] iPiece = new int[4][4];
        iPiece[0][1] = 1;
        iPiece[1][1] = 1;
        iPiece[2][1] = 1;
        iPiece[3][1] = 1;

        // Should fit vertically in the board
        boolean canPlace = !MatrixOperations.intersect(board, iPiece, 1, 5);
        assertTrue(canPlace, "I-piece should fit in board");
    }

    @Test
    @DisplayName("Brick should handle partial collisions")
    void testBrickPartialCollision() {
        // Fill one cell that brick would overlap
        board[3][12] = 1;

        // Try to place brick at (2, 10) - would partially overlap
        boolean collision = MatrixOperations.intersect(board, brick, 2, 10);
        assertTrue(collision, "Brick should detect even partial collisions");
    }

    @Test
    @DisplayName("Valid positions should work")
    void testValidPositions() {
        // Test valid positions
        assertTrue(!MatrixOperations.intersect(board, brick, 0, 0), "Position (0,0) should be valid");
        assertTrue(!MatrixOperations.intersect(board, brick, 2, 2), "Position (2,2) should be valid");
        assertTrue(!MatrixOperations.intersect(board, brick, 6, 5), "Position (6,5) should be valid");
    }

    @Test
    @DisplayName("Brick should handle board state changes")
    void testBoardStateChanges() {
        // Add obstacles to board
        board[4][12] = 1;
        board[4][13] = 1;

        // Brick should collide with obstacles at appropriate position
        boolean collision = MatrixOperations.intersect(board, brick, 3, 10);
        assertTrue(collision, "Should detect collision with obstacles");
    }
}
