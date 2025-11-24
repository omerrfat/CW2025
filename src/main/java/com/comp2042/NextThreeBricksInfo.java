package com.comp2042;

/**
 * Container for the next 3 bricks that will fall
 */
public final class NextThreeBricksInfo {

    private final int[][][] shapes; // Array of 3 brick shapes

    public NextThreeBricksInfo(int[][] brick1, int[][] brick2, int[][] brick3) {
        this.shapes = new int[3][][];
        this.shapes[0] = MatrixOperations.copy(brick1);
        this.shapes[1] = MatrixOperations.copy(brick2);
        this.shapes[2] = MatrixOperations.copy(brick3);
    }

    /**
     * Get the i-th next brick shape (0 = next, 1 = next+1, 2 = next+2)
     */
    public int[][] getBrickShape(int index) {
        if (index < 0 || index >= 3) {
            return new int[0][0];
        }
        return MatrixOperations.copy(shapes[index]);
    }

    /**
     * Get all three brick shapes
     */
    public int[][][] getAllShapes() {
        int[][][] copy = new int[3][][];
        for (int i = 0; i < 3; i++) {
            copy[i] = MatrixOperations.copy(shapes[i]);
        }
        return copy;
    }
}
