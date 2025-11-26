package com.comp2042;

public class GameController implements InputEventListener {

    private boolean paused = false;
    // `isGameOver` state is handled by GuiController; remove unused field
    private Board board = new SimpleBoard(25, 10);

    private GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    public void togglePause() {
        paused = !paused;
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
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
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    public int[][] getBoardMatrix() {
        return board.getBoardMatrix();
    }

    @Override
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
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    public void restartGame() {
        paused = false;
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        board.getScore().reset();
    }
}