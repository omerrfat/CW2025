package com.comp2042;

/**
 * NextShapeInfo - Immutable data class containing next brick information.
 * 
 * Holds:
 * - The 2D shape matrix of the next brick
 * - The rotation position (0-3 for standard Tetris pieces)
 * 
 * Provides safe access to shape data via copy to prevent external modification.
 * 
 * @author Umer Imran
 * @version 2.0
 */
public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    public int getPosition() {
        return position;
    }
}
