package com.comp2042;

import com.comp2042.util.Constants;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main GUI Controller for the Tetris game.
 * Handles rendering, user input, and game state management.
 */
public class GuiController implements Initializable {

    // ========== FXML COMPONENTS ==========
    @FXML
    private GridPane gamePanel;
    @FXML
    private Group groupNotification;
    @FXML
    private GridPane brickPanel;
    @FXML
    private GridPane nextPreview0;
    @FXML
    private GridPane nextPreview1;
    @FXML
    private GridPane nextPreview2;
    @FXML
    private GridPane holdPreview;
    @FXML
    private GameOverPanel gameOverPanel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label highScoreLabel;

    // ========== DISPLAY MATRICES ==========
    private Rectangle[][] displayMatrix; // Game board background
    private Rectangle[][] rectangles; // Current falling piece
    private Rectangle[][] nextBrickRectangles; // Next piece preview
    private Rectangle[][] holdBrickRectangles; // Hold piece preview

    // ========== GAME STATE ==========
    private InputEventListener eventListener;
    private Timeline timeLine;
    private final BooleanProperty isPause = new SimpleBooleanProperty(false);
    private final BooleanProperty isGameOver = new SimpleBooleanProperty(false);
    private int highScore = 0;

    // =============================================================================
    // INITIALIZATION
    // =============================================================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadFonts();
        setupKeyboardInput();
        gameOverPanel.setVisible(false);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
    }

    private void loadFonts() {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
    }

    private void setupKeyboardInput() {
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                handleKeyPress(keyEvent);
            }
        });
    }

    /**
     * Initialize the game view with board matrix and first brick
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        initializeBoard(boardMatrix);
        initializeCurrentBrick(brick);
        initializeNextBrickPreview(brick);
        initializeHoldPreview();
        startGameLoop();
    }

    private void initializeBoard(int[][] boardMatrix) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }
    }

    private void initializeCurrentBrick(ViewData brick) {
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        updateBrickPosition(brick);
    }

    private void startGameLoop() {
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(Constants.INITIAL_FALL_SPEED_MS),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    // =============================================================================
    // KEYBOARD INPUT HANDLING
    // =============================================================================

    private void handleKeyPress(KeyEvent keyEvent) {
        if (!isPause.getValue() && !isGameOver.getValue()) {
            handleGameControls(keyEvent);
        }
        handleMetaControls(keyEvent);
    }

    private void handleGameControls(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        if (code == KeyCode.LEFT || code == KeyCode.A) {
            refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
            keyEvent.consume();
        }
        if (code == KeyCode.RIGHT || code == KeyCode.D) {
            refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
            keyEvent.consume();
        }
        if (code == KeyCode.UP || code == KeyCode.W) {
            refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
            keyEvent.consume();
        }
        if (code == KeyCode.DOWN || code == KeyCode.S) {
            moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
            keyEvent.consume();
        }
        if (code == KeyCode.SPACE) {
            moveDownHard(new MoveEvent(EventType.DOWN, EventSource.USER));
            keyEvent.consume();
        }
        if (code == KeyCode.H) {
            refreshBrick(eventListener.onHoldEvent(new MoveEvent(EventType.DOWN, EventSource.USER)));
            keyEvent.consume();
        }
    }

    private void handleMetaControls(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        if (code == KeyCode.P) {
            togglePause();
            keyEvent.consume();
        }
        if (code == KeyCode.R || code == KeyCode.N) {
            newGame(null);
            keyEvent.consume();
        }
        if (code == KeyCode.ESCAPE) {
            openPauseMenu();
            keyEvent.consume();
        }
    }

    // =============================================================================
    // RENDERING - CURRENT BRICK
    // =============================================================================

    private void refreshBrick(ViewData brick) {
        if (brick == null || isPause.getValue()) {
            return;
        }

        updateCurrentBrick(brick);
        updateGhostPiece(brick);
        updateNextBrickPreview(brick);
        updateNextThreeBricksPreview(brick);

        if (!isGameOver.getValue()) {
            groupNotification.toFront();
        }
    }

    private void updateCurrentBrick(ViewData brick) {
        int[][] brickData = brick.getBrickData();

        // Reinitialize if dimensions changed (rotation or new piece)
        if (rectangles == null || rectangles.length != brickData.length ||
                rectangles[0].length != brickData[0].length) {
            reinitializeCurrentBrick(brickData);
        }

        updateBrickPosition(brick);
        updateBrickColors(brickData);
    }

    private void reinitializeCurrentBrick(int[][] brickData) {
        brickPanel.getChildren().clear();
        rectangles = new Rectangle[brickData.length][brickData[0].length];

        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                rectangle.setFill(getFillColor(brickData[i][j]));
                rectangle.setArcHeight(Constants.BRICK_ARC_SIZE);
                rectangle.setArcWidth(Constants.BRICK_ARC_SIZE);
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
    }

    private void updateBrickPosition(ViewData brick) {

        brickPanel.setLayoutX(gamePanel.getLayoutX() + Constants.BRICK_PANEL_X_OFFSET +
                brick.getxPosition() * (Constants.BRICK_SIZE + (int) gamePanel.getHgap()));
        brickPanel.setLayoutY(Constants.BRICK_PANEL_Y_OFFSET + gamePanel.getLayoutY() +
                brick.getyPosition() * (Constants.BRICK_SIZE + (int) gamePanel.getVgap()));
    }

    private void updateBrickColors(int[][] brickData) {
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                setRectangleData(brickData[i][j], rectangles[i][j]);
            }
        }
    }

    // =============================================================================
    // RENDERING - GHOST PIECE
    // =============================================================================

    private void updateGhostPiece(ViewData brick) {
        clearGhostPiece();

        int[][] ghost = brick.getGhostCoords();
        if (ghost != null) {
            drawGhostPiece(ghost);
        }
    }

    private void clearGhostPiece() {
        gamePanel.getChildren().removeIf(node -> node instanceof Rectangle &&
                node.getUserData() != null &&
                node.getUserData().equals("ghost"));
    }

    private void drawGhostPiece(int[][] ghostCoords) {
        for (int[] coord : ghostCoords) {
            int x = coord[1];
            int y = coord[0];

            Rectangle ghostBlock = createGhostBlock();
            GridPane.setColumnIndex(ghostBlock, x);
            GridPane.setRowIndex(ghostBlock, y - 2);
            gamePanel.getChildren().add(ghostBlock);
        }
    }

    private Rectangle createGhostBlock() {
        Rectangle ghostBlock = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
        ghostBlock.setFill(Constants.GHOST_FILL);
        ghostBlock.setStroke(Constants.GHOST_STROKE);
        ghostBlock.setStrokeWidth(Constants.GHOST_STROKE_WIDTH);
        ghostBlock.setUserData("ghost");
        ghostBlock.setMouseTransparent(true);
        return ghostBlock;
    }

    // =============================================================================
    // RENDERING - NEXT BRICK PREVIEW
    // =============================================================================

    private void initializeNextBrickPreview(ViewData brick) {
        if (nextPreview0 == null || brick == null || brick.getNextBrickData() == null) {
            return;
        }
        GridPane target = nextPreview0;
        target.getChildren().clear();
        int[][] nextBrickData = brick.getNextBrickData();
        nextBrickRectangles = new Rectangle[nextBrickData.length][nextBrickData[0].length];

        CenteringOffset offset = calculateCenteringOffset(nextBrickData);

        for (int i = 0; i < nextBrickData.length; i++) {
            for (int j = 0; j < nextBrickData[i].length; j++) {
                Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                rectangle.setFill(getFillColor(nextBrickData[i][j]));
                rectangle.setArcHeight(Constants.BRICK_ARC_SIZE);
                rectangle.setArcWidth(Constants.BRICK_ARC_SIZE);
                nextBrickRectangles[i][j] = rectangle;
                target.add(rectangle, j + offset.col, i + offset.row);
            }
        }
    }

    private void updateNextBrickPreview(ViewData brick) {
        if (nextPreview0 == null || brick == null || brick.getNextBrickData() == null) {
            return;
        }

        GridPane target = nextPreview0;
        int[][] nextBrickData = brick.getNextBrickData();

        // Reinitialize if dimensions changed
        if (nextBrickRectangles == null ||
                nextBrickRectangles.length != nextBrickData.length ||
                nextBrickRectangles[0].length != nextBrickData[0].length) {
            initializeNextBrickPreview(brick);
            return;
        }

        // Update colors only
        for (int i = 0; i < Math.min(nextBrickData.length, nextBrickRectangles.length); i++) {
            for (int j = 0; j < Math.min(nextBrickData[i].length, nextBrickRectangles[i].length); j++) {
                if (nextBrickRectangles[i][j] != null) {
                    setRectangleData(nextBrickData[i][j], nextBrickRectangles[i][j]);
                }
            }
        }
    }

    private CenteringOffset calculateCenteringOffset(int[][] brickData) {
        int minCol = brickData[0].length;
        int maxCol = -1;
        int minRow = brickData.length;
        int maxRow = -1;

        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    minRow = Math.min(minRow, i);
                    maxRow = Math.max(maxRow, i);
                    minCol = Math.min(minCol, j);
                    maxCol = Math.max(maxCol, j);
                }
            }
        }

        int pieceWidth = maxCol - minCol + 1;
        int pieceHeight = maxRow - minRow + 1;
        int offsetCol = (brickData[0].length - pieceWidth) / 2 - minCol;
        int offsetRow = (brickData.length - pieceHeight) / 2 - minRow;

        return new CenteringOffset(offsetRow, offsetCol);
    }

    // =============================================================================
    // RENDERING - HOLD PIECE PREVIEW
    // =============================================================================

    public void initializeHoldPreview() {
        if (holdPreview == null) {
            return;
        }
        holdPreview.getChildren().clear();
        holdBrickRectangles = new Rectangle[4][4];
    }

    public void updateHoldPreview(ViewData heldBrick) {
        if (holdPreview == null) {
            return;
        }

        // Clear previous hold display
        holdPreview.getChildren().clear();

        // If no brick is held, just show empty preview
        if (heldBrick == null) {
            return;
        }

        int[][] heldBrickData = heldBrick.getBrickData();
        holdBrickRectangles = new Rectangle[heldBrickData.length][heldBrickData[0].length];

        CenteringOffset offset = calculateCenteringOffset(heldBrickData);

        for (int i = 0; i < heldBrickData.length; i++) {
            for (int j = 0; j < heldBrickData[i].length; j++) {
                Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                rectangle.setFill(getFillColor(heldBrickData[i][j]));
                rectangle.setArcHeight(Constants.BRICK_ARC_SIZE);
                rectangle.setArcWidth(Constants.BRICK_ARC_SIZE);
                holdBrickRectangles[i][j] = rectangle;
                holdPreview.add(rectangle, j + offset.col, i + offset.row);
            }
        }
    }

    // =============================================================================
    // RENDERING - NEXT THREE BRICKS PREVIEW
    // =============================================================================

    private void updateNextThreeBricksPreview(ViewData brick) {
        if (brick == null || brick.getNextThreeBricksInfo() == null) {
            return;
        }

        NextThreeBricksInfo nextThreeBricksInfo = brick.getNextThreeBricksInfo();

        // display next 3 bricks vertically
        GridPane[] targets = new GridPane[] { nextPreview0, nextPreview1, nextPreview2 };

        for (int idx = 0; idx < targets.length; idx++) {
            GridPane target = targets[idx];
            if (target == null)
                continue;
            target.getChildren().clear();

            int[][] brickData = nextThreeBricksInfo.getBrickShape(idx);
            if (brickData == null || brickData.length == 0)
                continue;

            CenteringOffset offset = calculateCenteringOffset(brickData);

            for (int i = 0; i < brickData.length; i++) {
                for (int j = 0; j < brickData[i].length; j++) {
                    if (brickData[i][j] == 0)
                        continue;
                    Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                    rectangle.setFill(getFillColor(brickData[i][j]));
                    rectangle.setArcHeight(Constants.BRICK_ARC_SIZE);
                    rectangle.setArcWidth(Constants.BRICK_ARC_SIZE);
                    target.add(rectangle, j + Math.max(0, offset.col), i + Math.max(0, offset.row));
                }
            }
        }
    }

    private static class CenteringOffset {
        final int row;
        final int col;

        CenteringOffset(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    // =============================================================================
    // RENDERING - GAME BOARD BACKGROUND
    // =============================================================================

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    // =============================================================================
    // GAME LOGIC - PIECE MOVEMENT
    // =============================================================================

    private void moveDown(MoveEvent event) {
        if (isPause.getValue()) {
            return;
        }

        DownData downData = eventListener.onDownEvent(event);
        if (downData != null) {
            handleLineClears(downData);
            refreshBrick(downData.getViewData());
        }

        gamePanel.requestFocus();
        if (!isGameOver.getValue()) {
            groupNotification.toFront();
        }
    }

    private void moveDownHard(MoveEvent moveEvent) {
        DownData dd = eventListener.onHardDropEvent(moveEvent);
        if (dd != null) {
            refreshBrick(dd.getViewData());
            if (dd.getClearRow() != null && dd.getClearRow().getLinesRemoved() > 0) {
                refreshGameBackground(((GameController) eventListener).getBoardMatrix());
            }
        }
        if (!isGameOver.getValue()) {
            groupNotification.toFront();
        }
    }

    private void handleLineClears(DownData downData) {
        if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
            showLineClearNotification(downData.getClearRow().getScoreBonus());
        }
    }

    /**
     * flash the cleared rows (based on beforeMatrix which represents the board
     * AFTER merge but BEFORE clears).
     * after animation completes, it will invoke the provided onFinished callback
     */
    public void animateLineClear(ClearRow clearRow, int[][] beforeMatrix, Runnable onFinished) {
        if (clearRow == null || clearRow.getLinesRemoved() == 0 || beforeMatrix == null) {
            if (onFinished != null)
                Platform.runLater(onFinished);
            return;
        }

        int[] cleared = clearRow.getClearedRows();

        // Instead of animating the underlying board cells (which caused neighboring
        // rows to appear to flash), create overlay rectangles for the cleared rows
        // and animate those overlays only. Once overlays finish, remove them and
        // hide the underlying rows so the collapse animation can proceed without
        // overlap.
        Pane rootPane = (Pane) gamePanel.getParent();
        java.util.List<Rectangle> overlayRects = new java.util.ArrayList<>();
        java.util.List<Animation> overlayAnims = new java.util.ArrayList<>();

        double cellSize = Constants.BRICK_SIZE;
        double hGap = gamePanel.getHgap();
        double vGap = gamePanel.getVgap();

        for (int r : cleared) {
            if (r < 2 || r >= beforeMatrix.length)
                continue;
            for (int c = 0; c < beforeMatrix[r].length; c++) {
                int colorCode = beforeMatrix[r][c];
                if (colorCode == 0)
                    continue;

                Rectangle ov = new Rectangle(cellSize, cellSize);
                ov.setFill(getFillColor(colorCode));
                ov.setArcWidth(Constants.BRICK_ARC_SIZE);
                ov.setArcHeight(Constants.BRICK_ARC_SIZE);

                double originX = gamePanel.getLayoutX() + c * (cellSize + hGap);
                double originY = gamePanel.getLayoutY() + (r - 2) * (cellSize + vGap);
                ov.setLayoutX(originX);
                ov.setLayoutY(originY);
                ov.setMouseTransparent(true);

                overlayRects.add(ov);
                rootPane.getChildren().add(ov);

                // FillPulse -> fade out sequence for cleared row cells
                javafx.animation.FillTransition fill = new javafx.animation.FillTransition(Duration.millis(120), ov);
                fill.setFromValue((javafx.scene.paint.Color) getFillColor(colorCode));
                fill.setToValue(javafx.scene.paint.Color.WHITE);

                FadeTransition fade = new FadeTransition(Duration.millis(160), ov);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);

                javafx.animation.SequentialTransition seq = new javafx.animation.SequentialTransition(fill, fade);
                overlayAnims.add(seq);
            }
        }

        if (overlayAnims.isEmpty()) {
            if (onFinished != null)
                Platform.runLater(onFinished);
            return;
        }

        ParallelTransition pt = new ParallelTransition();
        pt.getChildren().addAll(overlayAnims);
        pt.setOnFinished(e -> {
            // overlays finished, remove them and hide the cleared rows underneath
            overlayRects.forEach(rootPane.getChildren()::remove);
            for (int r : cleared) {
                if (r < 2 || r >= displayMatrix.length)
                    continue;
                for (int c = 0; c < displayMatrix[r].length; c++) {
                    Rectangle rect = displayMatrix[r][c];
                    if (rect != null)
                        rect.setFill(javafx.scene.paint.Color.TRANSPARENT);
                }
            }

            // compute which rows will move (any row with cleared rows below it) and hide
            // their src cells
            int[] sortedCleared = java.util.Arrays.copyOf(cleared, cleared.length);
            java.util.Arrays.sort(sortedCleared);
            for (int r = 0; r < displayMatrix.length; r++) {
                int delta = 0;
                for (int cr : sortedCleared)
                    if (cr > r)
                        delta++;
                if (delta <= 0)
                    continue;
                if (r < 2 || r >= displayMatrix.length)
                    continue;
                for (int c = 0; c < displayMatrix[r].length; c++) {
                    Rectangle rect = displayMatrix[r][c];
                    if (rect != null)
                        rect.setFill(javafx.scene.paint.Color.TRANSPARENT);
                }
            }

            // now all moved sources and cleared rows are hidden — play the slide-down
            // overlays
            if (clearRow.getLinesRemoved() > 0) {
                RowCollapseAnimator.animateCollapse(rootPane, gamePanel, beforeMatrix, cleared, this::getFillColor,
                        () -> {
                            if (onFinished != null)
                                onFinished.run();
                        });
            } else {
                if (onFinished != null)
                    onFinished.run();
            }
        });
        pt.play();
    }

    private void showLineClearNotification(int score) {
        NotificationPanel notificationPanel = new NotificationPanel("+" + score);

        double panelW = gamePanel.getWidth();
        double panelH = gamePanel.getHeight();
        double x = gamePanel.getLayoutX() + (panelW - notificationPanel.getMinWidth()) / 2.0;
        double y = gamePanel.getLayoutY() + (panelH - notificationPanel.getMinHeight()) / 2.0;

        notificationPanel.setLayoutX(x);
        notificationPanel.setLayoutY(y);
        groupNotification.getChildren().add(notificationPanel);
        notificationPanel.showScore(groupNotification.getChildren());

        if (!isGameOver.getValue()) {
            groupNotification.toFront();
        }
    }

    // =============================================================================
    // UI - SCORE DISPLAY
    // =============================================================================

    public void showScoreBonus(int bonus) {
        Text popup = new Text("+" + bonus);
        popup.setFont(Font.font("Verdana", 22));
        popup.setFill(Color.WHITESMOKE);
        popup.setLayoutX(Constants.SCORE_POPUP_OFFSET_X);
        popup.setLayoutY(Constants.SCORE_POPUP_OFFSET_Y);

        Pane rootPane = (Pane) gamePanel.getParent();
        rootPane.getChildren().add(popup);

        animateScorePopup(popup, rootPane);
    }

    private void animateScorePopup(Text popup, Pane rootPane) {
        TranslateTransition moveUp = new TranslateTransition(Duration.millis(1000), popup);
        moveUp.setByY(Constants.SCORE_POPUP_MOVE_Y);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), popup);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        moveUp.play();
        fadeOut.play();

        fadeOut.setOnFinished(e -> rootPane.getChildren().remove(popup));
    }

    public void bindScore(IntegerProperty integerProperty) {
        if (scoreLabel.textProperty().isBound()) {
            scoreLabel.textProperty().unbind();
        }
        scoreLabel.textProperty().bind(integerProperty.asString());

        // Listen for score changes to update high score
        integerProperty.addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() > highScore) {
                highScore = newVal.intValue();
                highScoreLabel.setText(String.valueOf(highScore));
            }
        });
    }

    // =============================================================================
    // GAME STATE MANAGEMENT
    // =============================================================================

    public void gameOver() {
        timeLine.stop();
        groupNotification.getChildren().removeIf(node -> node instanceof NotificationPanel);
        gamePanel.setOpacity(Constants.PAUSED_OPACITY);
        gameOverPanel.setVisible(true);
        gameOverPanel.toFront();
        isGameOver.setValue(true);

        // Add dramatic screen shake animation
        animateScreenShake(gamePanel);

        // Add game over text pulse animation
        gameOverPanel.playGameOverAnimation();
    }

    /**
     * Animate a dramatic screen shake effect for game over
     */
    private void animateScreenShake(javafx.scene.layout.GridPane target) {
        javafx.animation.Timeline shakeTimeline = new javafx.animation.Timeline();

        // Create multiple rapid position changes for shake effect
        double shakeDistance = 8.0;
        javafx.util.Duration shakeDuration = javafx.util.Duration.millis(50);

        // Add keyframes for shake animation
        shakeTimeline.getKeyFrames().addAll(
                new javafx.animation.KeyFrame(javafx.util.Duration.ZERO,
                        new javafx.animation.KeyValue(target.translateXProperty(), 0)),
                new javafx.animation.KeyFrame(shakeDuration,
                        new javafx.animation.KeyValue(target.translateXProperty(), shakeDistance)),
                new javafx.animation.KeyFrame(shakeDuration.multiply(2),
                        new javafx.animation.KeyValue(target.translateXProperty(), -shakeDistance)),
                new javafx.animation.KeyFrame(shakeDuration.multiply(3),
                        new javafx.animation.KeyValue(target.translateXProperty(), shakeDistance)),
                new javafx.animation.KeyFrame(shakeDuration.multiply(4),
                        new javafx.animation.KeyValue(target.translateXProperty(), -shakeDistance)),
                new javafx.animation.KeyFrame(shakeDuration.multiply(5),
                        new javafx.animation.KeyValue(target.translateXProperty(), shakeDistance)),
                new javafx.animation.KeyFrame(shakeDuration.multiply(6),
                        new javafx.animation.KeyValue(target.translateXProperty(), -shakeDistance)),
                new javafx.animation.KeyFrame(shakeDuration.multiply(7),
                        new javafx.animation.KeyValue(target.translateXProperty(), shakeDistance)),
                new javafx.animation.KeyFrame(shakeDuration.multiply(8),
                        new javafx.animation.KeyValue(target.translateXProperty(), 0)));

        shakeTimeline.play();
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(false);
        isGameOver.setValue(false);
        gamePanel.setOpacity(Constants.ACTIVE_OPACITY);
    }

    public void togglePause() {
        if (isPause.getValue()) {
            resumeGame();
        } else {
            pauseGame();
        }
        gamePanel.requestFocus();
    }

    private void pauseGame() {
        timeLine.pause();
        isPause.setValue(true);

        NotificationPanel pausedMsg = new NotificationPanel("PAUSED");
        groupNotification.getChildren().add(pausedMsg);
        pausedMsg.showScore(groupNotification.getChildren());

        gamePanel.setOpacity(Constants.PAUSED_OPACITY);
    }

    private void resumeGame() {
        timeLine.play();
        isPause.setValue(false);
        groupNotification.getChildren().removeIf(node -> node instanceof NotificationPanel);
        gamePanel.setOpacity(Constants.ACTIVE_OPACITY);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }

    /**
     * Open the pause menu dialog (triggered by ESC key).
     */
    private void openPauseMenu() {
        if (isGameOver.getValue()) {
            return;
        }

        // Pause the game first
        pauseGame();

        try {
            java.net.URL location = getClass().getClassLoader().getResource("pauseMenu.fxml");
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(location);
            javafx.scene.Parent root = fxmlLoader.load();
            PauseMenuController pauseController = fxmlLoader.getController();

            javafx.stage.Stage pauseStage = new javafx.stage.Stage();
            pauseStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            pauseStage.setScene(new javafx.scene.Scene(root));
            pauseStage.setTitle("Pause Menu");

            // Get the main game window to set owner and modality
            javafx.stage.Stage gameStage = (javafx.stage.Stage) gamePanel.getScene().getWindow();
            pauseStage.initOwner(gameStage);
            pauseStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            // Pass context to pause controller
            pauseController.setPauseContext(this, pauseStage);

            // Handle ESC key on pause menu
            root.setOnKeyPressed(pauseController::handleKeyEvent);

            pauseStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Resume game from pause menu.
     */
    public void resumeFromPause() {
        resumeGame();
    }

    // =============================================================================
    // UTILITIES
    // =============================================================================

    private Paint getFillColor(int colorCode) {
        return Constants.PieceColors.getColor(colorCode);
    }

    private void setRectangleData(int colorCode, Rectangle rectangle) {
        rectangle.setFill(getFillColor(colorCode));
        rectangle.setArcHeight(Constants.BRICK_ARC_SIZE);
        rectangle.setArcWidth(Constants.BRICK_ARC_SIZE);
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }
}