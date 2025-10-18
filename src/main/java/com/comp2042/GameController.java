package com.comp2042;

public class GameController implements InputEventListener {

    private boolean paused = false; // to show paused state of the game
    private boolean gameOver = false;
    private Board board = new SimpleBoard(25, 10);

    private GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    // Logic for passing pause
    public void togglePause() {
        paused = !paused;
    }
    /**
     * @return paused returns paused if the game is paused
     */
    public boolean isPaused() {
        return paused;
    }


    @Override
    public DownData onDownEvent(MoveEvent event) {
        if (paused) return null; // ignore if paused

        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    //method that resets a game, with score beginning from 0
    public void restartGame() {
        paused = false;
        gameOver = false;
        board.newGame();  // resets the board
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        board.getScore().reset();
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        if (paused) return null;

        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        if (paused) return null;

        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        if (paused) return null;

        board.rotateLeftBrick();
        return board.getViewData();
    }


    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    // because board is a private variable, adding a getter for boardMatrix without breaking encapsulation
    public int[][] getBoardMatrix() {
        return board.getBoardMatrix();
    }


    // new override on implementing hard drop
    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        int dropDistance = 0;
        while (board.moveBrickDown()) {
            dropDistance++;
        }

        // merge the piece and clear row
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }

        // bonus points for hard drop distance (optional)
        board.getScore().add(dropDistance * 2);

        // spawn next brick
        if (board.createNewBrick()) {
            viewGuiController.gameOver();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());

        return new DownData(clearRow, board.getViewData());

    }
}
