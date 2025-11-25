package com.comp2042;

import com.comp2042.util.Constants;
import javafx.animation.FadeTransition;
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
 * handles rendering, user input, and game state management.
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
    private GameOverPanel gameOverPanel;
    @FXML
    private Label scoreLabel;

    // ========== DISPLAY MATRICES ==========
    private Rectangle[][] displayMatrix; // Game board background
    private Rectangle[][] rectangles; // Current falling piece
    private Rectangle[][] nextBrickRectangles; // Next piece preview

    // ========== GAME STATE ==========
    private InputEventListener eventListener;
    private Timeline timeLine;
    private final BooleanProperty isPause = new SimpleBooleanProperty(false);
    private final BooleanProperty isGameOver = new SimpleBooleanProperty(false);

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