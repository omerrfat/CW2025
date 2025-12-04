package com.comp2042;

import com.comp2042.game.ObstacleManager;
import com.comp2042.logic.*;
import com.comp2042.event.*;
import com.comp2042.dto.*;

/**
 * Main Game Controller - Orchestrates game logic and state.
 * 
 * Responsible for:
 * - Handling all game events from user input
 * - Communicating with the Board for game mechanics
 * - Updating the GUI with game state changes
 * - Managing score tracking
 * - Handling game over and restart conditions
 * - Managing Obstacle Mode challenges
 * 
 * Implements InputEventListener to receive all input events
 * (movement, rotation, hard drop, hold).
 * 
 * @author Umer Imran
 * @version 2.1
 */
public class GameController implements InputEventListener {

    private boolean paused = false;
    // `isGameOver` state is handled by GuiController; remove unused field
    private Board board = new SimpleBoard(25, 10);

    private GuiController viewGuiController;
    private ObstacleManager obstacleManager;
    private boolean obstacleMode = false;

    /**
     * Initializes the GameController with UI reference and initial board state.
     * 
     * @param c The GuiController instance for rendering updates
     */
    public GameController(GuiController c) {
        viewGuiController = c;
        obstacleManager = new ObstacleManager();
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    /**
     * Enables Obstacle Mode and generates obstacles based on difficulty level.
     * 
     * @param difficultyLevel the current game difficulty (1, 5, 10, or 15)
     */
    public void enableObstacleMode(int difficultyLevel) {
        this.obstacleMode = true;

        // Generate and place obstacles
        int[][] obstacles = obstacleManager.generateObstacles(difficultyLevel);
        ObstacleManager.placeObstacles(board.getBoardMatrix(), obstacles);

        // Refresh the display to show obstacles
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    /**
     * Check if Obstacle Mode is enabled.
     * 
     * @return true if in obstacle mode
     */
    public boolean isObstacleMode() {
        return obstacleMode;
    }

    /**
     * Toggles the pause state of the game.
     */
    public void togglePause() {
        paused = !paused;
    }

    /**
     * Returns whether the game is currently paused.
     * 
     * @return true if paused, false otherwise
     */
    public boolean isPaused() {
        return paused;
    }

    @Override
    /**
     * Handles down movement event (gravity or manual down press).
     * 
     * @param event The move event from player or game loop
     * @return DownData containing updated board state and any line clears
     */
    public DownData onDownEvent(MoveEvent event) {
        // REMOVED: if (paused) return null;
        // Let GuiController handle pause checks instead

        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {

            board.mergeBrickToBackground();
            // snapshot the board AFTER merge, but BEFORE removal so the animation can flash
            // the rows
            int[][] beforeClear = MatrixOperations.copy(board.getBoardMatrix());
            clearRow = board.clearRows();

            if (clearRow != null && clearRow.getLinesRemoved() > 0) {
                int bonus = clearRow.getScoreBonus();
                board.getScore().add(bonus);
                viewGuiController.showScoreBonus(bonus);
            }

            boolean gameOver = board.createNewBrick();
            if (gameOver) {
                viewGuiController.gameOver();
            }

            // animate the cleared rows (if any) using the before-clear snapshot and then
            // refresh
            if (clearRow != null && clearRow.getLinesRemoved() > 0) {
                viewGuiController.animateLineClear(clearRow, beforeClear,
                        () -> viewGuiController.refreshGameBackground(board.getBoardMatrix()));
            } else {
                viewGuiController.refreshGameBackground(board.getBoardMatrix());
            }

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }

        // debug prints removed
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    /**
     * Creates a new game session, resets board and score.
     */
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    /**
     * Retrieves the current board matrix state.
     * 
     * @return The 25x10 game board matrix
     */
    public int[][] getBoardMatrix() {
        return board.getBoardMatrix();
    }

    @Override
    /**
     * Handles hard drop event (brick instantly falls to bottom).
     * 
     * @param event The hard drop move event
     * @return DownData containing updated board state and any line clears
     */
    public DownData onHardDropEvent(MoveEvent event) {
        int dropDistance = 0;
        while (board.moveBrickDown()) {
            dropDistance++;
        }

        board.mergeBrickToBackground();
        int[][] beforeClear = MatrixOperations.copy(board.getBoardMatrix());
        ClearRow clearRow = board.clearRows();

        int totalBonus = dropDistance * 2; // start with hard drop bonus

        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            int lineBonus = clearRow.getScoreBonus();
            board.getScore().add(lineBonus);
            totalBonus += lineBonus; // add line clear bonus to total
        }

        board.getScore().add(dropDistance * 2);
        viewGuiController.showScoreBonus(totalBonus); // Show combined bonus

        boolean gameOver = board.createNewBrick();
        if (gameOver) {
            viewGuiController.gameOver();
        }

        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            viewGuiController.animateLineClear(clearRow, beforeClear,
                    () -> viewGuiController.refreshGameBackground(board.getBoardMatrix()));
        } else {
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        }

        return new DownData(clearRow, board.getViewData());
    }

    @Override
    /**
     * Handles left movement event.
     * 
     * @param event The left move event
     * @return ViewData containing updated brick position
     */
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    /**
     * Handles right movement event.
     * 
     * @param event The right move event
     * @return ViewData containing updated brick position
     */
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    /**
     * Handles brick rotation event.
     * 
     * @param event The rotate move event
     * @return ViewData containing updated rotated brick
     */
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    /**
     * Handles hold piece swap event.
     * 
     * @param event The hold move event
     * @return ViewData containing the swapped brick now in play
     */
    public ViewData onHoldEvent(MoveEvent event) {
        ViewData viewData = board.holdPiece();
        ViewData heldData = board.getHeldPiece();
        viewGuiController.updateHoldPreview(heldData);
        return viewData;
    }

    /**
     * Restarts the game - resets board, score, and pause state.
     * Called when returning from game over to main menu.
     */
    public void restartGame() {
        paused = false;
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        board.getScore().reset();
    }
}