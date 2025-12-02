package com.comp2042;

/**
 * Board Interface - Defines the contract for game board implementations.
 * 
 * Provides methods for:
 * - Brick movement (left, right, down)
 * - Brick rotation
 * - Brick spawning
 * - Row clearing with scoring
 * - Held piece management
 * 
 * @author Umer Imran
 * @version 2.0
 */
public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();

    ViewData holdPiece();

    ViewData getHeldPiece();
}
