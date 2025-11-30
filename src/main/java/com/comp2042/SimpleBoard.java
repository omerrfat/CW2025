package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import java.awt.*;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private Brick currentBrick;
    private Brick nextBrick; // stores the next piece
    private Brick heldBrick; // stores the held piece for hold feature

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
        heldBrick = null; // Initialize held brick as empty
        // Initialize: set the first "next" brick (current will be created
        // later when the game starts via createNewBrick())
        nextBrick = brickGenerator.getBrick(); // consume first into preview
    }

    private boolean canMove(int[][] shape, int newRow, int newCol) {
        // returns true if the piece can move down to (newRow, newCol)
        return !MatrixOperations.intersect(currentGameMatrix, shape, newCol, newRow);
    }

    /**
     * calculate where the current piece would land if dropped instantly.
     */
    public int[][] getGhostBrickCoordinates() {
        if (currentBrick == null || currentOffset == null)
            return new int[0][0];

        int[][] shape = brickRotator.getCurrentShape();
        int ghostRow = (int) currentOffset.getY();
        int col = (int) currentOffset.getX();

        // Drop down until it collides
        while (canMove(shape, ghostRow + 1, col)) {
            ghostRow++;
        }

        // convert ghost shape into coordinates for GUI
        return MatrixOperations.getOccupiedCells(shape, col, ghostRow);
    }

    @Override
    public boolean moveBrickDown() {
        int[][] shape = brickRotator.getCurrentShape();
        Point newOffset = new Point(currentOffset);
        newOffset.translate(0, 1);

        boolean conflict = MatrixOperations.intersect(currentGameMatrix, shape,
                (int) newOffset.getX(), (int) newOffset.getY());

        if (conflict)
            return false;

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

        if (conflict)
            return false;

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

        if (conflict)
            return false;

        currentOffset = newOffset;
        return true;
    }

    @Override
    public boolean rotateLeftBrick() {
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, nextShape.getShape(),
                (int) currentOffset.getX(), (int) currentOffset.getY());

        if (conflict)
            return false;

        brickRotator.setCurrentShape(nextShape.getPosition());
        return true;
    }

    @Override
    public boolean createNewBrick() {
        // Move the previewed next brick into the current piece and then
        // poll the generator for the next preview piece. This preserves
        // the expected createNewBrick() workflow used by GameController.
        currentBrick = nextBrick;
        nextBrick = brickGenerator.getBrick(); // poll next piece into preview

        // apply the consumed brick as the current falling piece
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
            nextBrickShape = nextBrick.getShapeMatrix().getFirst(); // Get first rotation state
        }

        // Get the next 3 bricks for the preview panel
        NextThreeBricksInfo nextThreeBricksInfo = getNextThreeBricksInfo();

        ViewData viewData = new ViewData(
                shape,
                (int) currentOffset.getX(),
                (int) currentOffset.getY(),
                nextBrickShape,
                nextThreeBricksInfo);

        // Add ghost info
        viewData.setGhostCoords(getGhostBrickCoordinates());
        return viewData;
    }

    /**
     * Get the next 3 bricks information for the preview panel
     */
    private NextThreeBricksInfo getNextThreeBricksInfo() {
        // Build preview list where first preview item is the stored nextBrick,
        // followed by the first two bricks from the generator's upcoming queue.
        Brick[] peekTwo = brickGenerator.peekNext(2);

        Brick b1 = nextBrick != null ? nextBrick : (peekTwo.length > 0 ? peekTwo[0] : null);
        Brick b2 = (peekTwo.length > 0) ? peekTwo[0] : null;
        Brick b3 = (peekTwo.length > 1) ? peekTwo[1] : null;

        int[][] brick1 = (b1 != null && b1.getShapeMatrix() != null && !b1.getShapeMatrix().isEmpty())
                ? b1.getShapeMatrix().getFirst()
                : new int[4][4];

        int[][] brick2 = (b2 != null && b2.getShapeMatrix() != null && !b2.getShapeMatrix().isEmpty())
                ? b2.getShapeMatrix().getFirst()
                : new int[4][4];

        int[][] brick3 = (b3 != null && b3.getShapeMatrix() != null && !b3.getShapeMatrix().isEmpty())
                ? b3.getShapeMatrix().getFirst()
                : new int[4][4];

        return new NextThreeBricksInfo(brick1, brick2, brick3);
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
        // Reset the brick generator for a fresh queue
        brickGenerator = new RandomBrickGenerator();
        // Set first preview brick and create the initial current piece
        nextBrick = brickGenerator.getBrick();
        heldBrick = null; // Reset held brick for new game
        createNewBrick();
    }
    /**
     * Hold the current brick and swap it with the held brick.
     */
    @Override
    public ViewData holdPiece() {
        if (currentBrick == null) {
            return getViewData(); // Can't hold if no current piece
        }

        // Swap current brick with held brick
        Brick temp = currentBrick;
        currentBrick = heldBrick;
        heldBrick = temp;

        // If current brick is now null (first hold), take the next piece
        if (currentBrick == null) {
            currentBrick = nextBrick;
            nextBrick = brickGenerator.getBrick();
        }

        // Reset the rotation state and position for the new current brick
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(4, 0);

        return getViewData();
    }

    @Override
    public ViewData getHeldPiece() {
        if (heldBrick == null) {
            return null;
        }

        // Create ViewData for the held piece with its first rotation state
        int[][] heldBrickShape = null;
        if (heldBrick.getShapeMatrix() != null && !heldBrick.getShapeMatrix().isEmpty()) {
            heldBrickShape = heldBrick.getShapeMatrix().get(0);
        }

        // Position at (0,0) since held brick is displayed separately, not on board
        return new ViewData(heldBrickShape, 0, 0, null);
    }
}