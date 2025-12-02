package com.comp2042;

/**
 * ViewData - Immutable data class containing current game state for UI
 * rendering.
 * 
 * Holds:
 * - Current brick data and position (x, y coordinates)
 * - Next brick data for preview
 * - Information about next three bricks
 * - Ghost piece coordinates (fall projection)
 * - Board state for rendering
 * 
 * Provides safe access to data via copy to prevent external modification.
 * Designed for efficient communication between game logic and UI rendering.
 * 
 * @author Umer Imran
 * @version 2.0
 */
public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData;
    private final NextThreeBricksInfo nextThreeBricksInfo;

    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this(brickData, xPosition, yPosition, nextBrickData, null);
    }

    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData,
            NextThreeBricksInfo nextThreeBricksInfo) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.nextThreeBricksInfo = nextThreeBricksInfo;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }

    public NextThreeBricksInfo getNextThreeBricksInfo() {
        return nextThreeBricksInfo;
    }

    /**
     * Getter and Setter for GhostCoords
     */
    private int[][] ghostCoords;

    public int[][] getGhostCoords() {
        return ghostCoords;
    }

    public void setGhostCoords(int[][] ghostCoords) {
        this.ghostCoords = ghostCoords;
    }
}
