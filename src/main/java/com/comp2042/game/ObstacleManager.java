package com.comp2042.game;

import java.util.Random;
import java.util.HashSet;
import java.util.Set;

/**
 * ObstacleManager - Manages obstacle generation and placement in Obstacle Mode.
 * 
 * In Obstacle Mode, random obstacles spawn on the board creating additional
 * challenges.
 * Players must navigate around these obstacles while placing their falling
 * bricks.
 * 
 * Responsibilities:
 * - Generate random obstacles at the start of the game
 * - Determine obstacle density based on difficulty level
 * - Provide obstacle positions to the GUI for rendering
 * - Store obstacles in the board matrix
 * 
 * Obstacles are represented by a special color code (8) to distinguish them
 * from regular pieces.
 * 
 * @author Umer Imran
 * @version 1.0
 */
public class ObstacleManager {

    private static final int OBSTACLE_COLOR_CODE = 8; // Special color for obstacles
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 25;
    private static final int HIDDEN_ROWS = 5; // Don't place obstacles in hidden rows

    private Random random;
    private Set<String> obstaclePositions; // Store as "row,col" strings

    public ObstacleManager() {
        this.random = new Random();
        this.obstaclePositions = new HashSet<>();
    }

    /**
     * Generates obstacles for the board based on difficulty level.
     * Higher difficulty = more obstacles
     * 
     * Level 1: 2-4 obstacles
     * Level 5: 5-8 obstacles
     * Level 10: 8-12 obstacles
     * Level 15: 12-16 obstacles
     * 
     * @param difficultyLevel the current difficulty level (1, 5, 10, or 15)
     * @return a 2D array of new obstacles to place on the board
     */
    public int[][] generateObstacles(int difficultyLevel) {
        obstaclePositions.clear();

        int obstacleCount = calculateObstacleCount(difficultyLevel);
        int[][] obstacles = new int[obstacleCount][2]; // [row, col]

        int placed = 0;
        int attempts = 0;
        int maxAttempts = obstacleCount * 10; // Prevent infinite loops

        while (placed < obstacleCount && attempts < maxAttempts) {
            // Place obstacles in lower half of board (rows 15-24, visible area)
            int row = HIDDEN_ROWS + random.nextInt(BOARD_HEIGHT - HIDDEN_ROWS - 2);
            int col = random.nextInt(BOARD_WIDTH);

            String position = row + "," + col;

            // Avoid duplicate obstacles
            if (!obstaclePositions.contains(position)) {
                obstaclePositions.add(position);
                obstacles[placed][0] = row;
                obstacles[placed][1] = col;
                placed++;
            }

            attempts++;
        }

        return obstacles;
    }

    /**
     * Calculate how many obstacles should be placed based on difficulty.
     * 
     * @param difficultyLevel the current difficulty level
     * @return number of obstacles to generate
     */
    private int calculateObstacleCount(int difficultyLevel) {
        switch (difficultyLevel) {
            case 1:
                return 2 + random.nextInt(3); // 2-4 obstacles
            case 5:
                return 5 + random.nextInt(4); // 5-8 obstacles
            case 10:
                return 8 + random.nextInt(5); // 8-12 obstacles
            case 15:
                return 12 + random.nextInt(5); // 12-16 obstacles
            default:
                return Math.max(1, difficultyLevel / 2); // Fallback formula
        }
    }

    /**
     * Place obstacles directly into the board matrix.
     * 
     * @param boardMatrix the game board to modify
     * @param obstacles   array of [row, col] positions for obstacles
     */
    public static void placeObstacles(int[][] boardMatrix, int[][] obstacles) {
        if (obstacles == null) {
            return;
        }

        for (int[] obstacle : obstacles) {
            int row = obstacle[0];
            int col = obstacle[1];

            // Validate bounds
            if (row >= 0 && row < boardMatrix.length && col >= 0 && col < boardMatrix[0].length) {
                boardMatrix[row][col] = OBSTACLE_COLOR_CODE;
            }
        }
    }

    /**
     * Gets the color code for obstacles.
     * Used by the GUI to render obstacles with a distinct color.
     * 
     * @return the color code (8) for obstacles
     */
    public static int getObstacleColorCode() {
        return OBSTACLE_COLOR_CODE;
    }

    /**
     * Check if a position contains an obstacle.
     * 
     * @param boardMatrix the game board
     * @param row         the row to check
     * @param col         the column to check
     * @return true if the position has an obstacle
     */
    public static boolean isObstacle(int[][] boardMatrix, int row, int col) {
        if (row < 0 || row >= boardMatrix.length || col < 0 || col >= boardMatrix[0].length) {
            return false;
        }
        return boardMatrix[row][col] == OBSTACLE_COLOR_CODE;
    }

    /**
     * Get all current obstacle positions.
     * 
     * @return set of obstacle positions as "row,col" strings
     */
    public Set<String> getObstaclePositions() {
        return new HashSet<>(obstaclePositions);
    }
}
