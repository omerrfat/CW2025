package com.comp2042;

import com.comp2042.logic.bricks.Brick;

/**
 * BrickRotator - Manages brick rotation state and calculations.
 * 
 * Responsibilities:
 * - Tracks current rotation state of active brick
 * - Calculates next rotation shape in sequence
 * - Handles rotation wrap-around (4 rotations per standard Tetris piece)
 * - Stores reference to next brick for lookahead
 * 
 * @author Umer Imran
 * @version 2.0
 */
public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;
    private Brick nextBrick;

    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }

    /**
     *
     * @return ghost of the next brick on the bottom
     */
    public int[][] getNextBrickShape() {
        if (nextBrick == null)
            return new int[0][0];
        return nextBrick.getShapeMatrix().get(0);
    }

}
