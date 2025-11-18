package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import java.awt.*;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private Brick currentBrick;
    private Brick nextBrick;  // stores the next piece

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
        nextBrick = brickGenerator.getBrick();  // generate first next brick
    }

    private boolean canMove(int[][] shape, int newRow, int newCol) {
        // returns true if the piece can move down to (newRow, newCol)
        return !MatrixOperations.intersect(currentGameMatrix, shape, newCol, newRow);
    }

    /**
     * calculate where the current piece would land if dropped instantly.
     */
    public int[][] getGhostBrickCoordinates() {
        if (currentBrick == null || currentOffset == null) return new int[0][0];

        int[][] shape = brickRotator.getCurrentShape();
        int ghostRow = (int) currentOffset.getY();
        int col = (int) currentOffset.getX();

        // Drop down until it collides
        while (canMove(shape, ghostRow + 1, col)) {
            ghostRow++;
        }

        // convert ghost shape into coordinates for GUI
        int[][] ghostCoords = MatrixOperations.getOccupiedCells(shape, col, ghostRow);
        return ghostCoords;
    }

    @Override
    public boolean moveBrickDown() {
        int[][] shape = brickRotator.getCurrentShape();
        Point newOffset = new Point(currentOffset);
        newOffset.translate(0, 1);

        boolean conflict = MatrixOperations.intersect(currentGameMatrix, shape,
                (int) newOffset.getX(), (int) newOffset.getY());

        if (conflict) return false;

        currentOffset = newOffset;
        return true;
    }

    @Override
    public boolean moveBrickLeft() {
        int[][] shape = brickRotator.getCurrentShape();
        Point newOffset = new Point(currentOffset);
        newOffset.translate(-1, 0);

        boolean conflict = MatrixOperations.intersect(currentGameMatrix, shape,
                (int) newOffset.getX(), (int) newOffset.getY());

        if (conflict) return false;

        currentOffset = newOffset;
        return true;
    }

    @Override
    public boolean moveBrickRight() {
        int[][] shape = brickRotator.getCurrentShape();
        Point newOffset = new Point(currentOffset);
        newOffset.translate(1, 0);

        boolean conflict = MatrixOperations.intersect(currentGameMatrix, shape,
                (int) newOffset.getX(), (int) newOffset.getY());

        if (conflict) return false;

        currentOffset = newOffset;
        return true;
    }

    @Override
    public boolean rotateLeftBrick() {
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, nextShape.getShape(),
                (int) currentOffset.getX(), (int) currentOffset.getY());

        if (conflict) return false;

        brickRotator.setCurrentShape(nextShape.getPosition());
        return true;
    }

    @Override
    public boolean createNewBrick() {
        // UPDATED: Use the stored nextBrick instead of generating new one
        currentBrick = nextBrick;
        nextBrick = brickGenerator.getBrick();  // Generate the new next brick

        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(4, 0); // typically start near top

        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(),
                (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        int[][] shape = brickRotator.getCurrentShape();

        // Get the next brick shape directly from the nextBrick object
        int[][] nextBrickShape = null;
        if (nextBrick != null && nextBrick.getShapeMatrix() != null && !nextBrick.getShapeMatrix().isEmpty()) {
            nextBrickShape = nextBrick.getShapeMatrix().get(0);  // Get first rotation state
        }

        ViewData viewData = new ViewData(
                shape,
                (int) currentOffset.getX(),
                (int) currentOffset.getY(),
                nextBrickShape
        );

        // Add ghost info
        viewData.setGhostCoords(getGhostBrickCoordinates());
        return viewData;
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(),
                (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        nextBrick = brickGenerator.getBrick();  // Generate new next brick for new game
        createNewBrick();
    }
}