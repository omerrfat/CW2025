package com.comp2042;

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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
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

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GridPane nextBrickPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private Label scoreLabel;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Rectangle[][] nextBrickRectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private Group ghostLayer = new Group();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {

                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                }

                // pause toggle key
                if (keyEvent.getCode() == KeyCode.P) {
                    togglePause();
                    keyEvent.consume();
                }

                // restart key
                if (keyEvent.getCode() == KeyCode.R) {
                    newGame(null);
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
                // using key Space to move down hard
                if (keyEvent.getCode() == KeyCode.SPACE) {
                    moveDownHard(new MoveEvent(EventType.DOWN, EventSource.USER));
                    keyEvent.consume();
                }
            }

        });
        gameOverPanel.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);

        ghostLayer.setMouseTransparent(true);
        gamePanel.getChildren().add(ghostLayer);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

        // Initialize the next brick preview
        initNextBrickPreview(brick);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    /**
     * Initialize the next brick preview panel
     */
    private void initNextBrickPreview(ViewData brick) {
        if (nextBrickPanel == null || brick == null || brick.getNextBrickData() == null) {
            return;
        }

        // Clear existing rectangles
        nextBrickPanel.getChildren().clear();

        int[][] nextBrickData = brick.getNextBrickData();
        nextBrickRectangles = new Rectangle[nextBrickData.length][nextBrickData[0].length];

        for (int i = 0; i < nextBrickData.length; i++) {
            for (int j = 0; j < nextBrickData[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(nextBrickData[i][j]));
                rectangle.setArcHeight(9);
                rectangle.setArcWidth(9);
                nextBrickRectangles[i][j] = rectangle;
                nextBrickPanel.add(rectangle, j, i);
            }
        }
    }

    /**
     * Update the next brick preview when it changes
     */
    private void updateNextBrickPreview(ViewData brick) {
        if (nextBrickPanel == null || brick == null || brick.getNextBrickData() == null) {
            return;
        }

        int[][] nextBrickData = brick.getNextBrickData();

        // If dimensions changed, reinitialize
        if (nextBrickRectangles == null ||
                nextBrickRectangles.length != nextBrickData.length ||
                nextBrickRectangles[0].length != nextBrickData[0].length) {
            initNextBrickPreview(brick);
            return;
        }

        // Update existing rectangles
        for (int i = 0; i < nextBrickData.length; i++) {
            for (int j = 0; j < nextBrickData[i].length; j++) {
                setRectangleData(nextBrickData[i][j], nextBrickRectangles[i][j]);
            }
        }
    }

    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.TRANSPARENT;
                break;
        }
        return returnPaint;
    }


    private void refreshBrick(ViewData brick) {
        if (brick == null) {
            return; // Early return if brick is null
        }

        if (isPause.getValue() == Boolean.FALSE) {
            // Check if brick dimensions have changed and reinitialize if needed
            int[][] brickData = brick.getBrickData();
            if (rectangles == null || rectangles.length != brickData.length ||
                    rectangles[0].length != brickData[0].length) {
                // Reinitialize rectangles array if dimensions changed
                brickPanel.getChildren().clear();
                rectangles = new Rectangle[brickData.length][brickData[0].length];
                for (int i = 0; i < brickData.length; i++) {
                    for (int j = 0; j < brickData[i].length; j++) {
                        Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                        rectangle.setFill(getFillColor(brickData[i][j]));
                        rectangles[i][j] = rectangle;
                        brickPanel.add(rectangle, j, i);
                    }
                }
            }

            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brickData.length; i++) {
                for (int j = 0; j < brickData[i].length; j++) {
                    setRectangleData(brickData[i][j], rectangles[i][j]);
                }
            }

            // Update next brick preview
            updateNextBrickPreview(brick);
        }
        // --- GHOST PIECE (SHADOW) DRAWING ---
        int[][] ghost = brick.getGhostCoords();

        // remove any previously drawn ghost blocks that were added directly to the GridPane
        gamePanel.getChildren().removeIf(node ->
                node instanceof Rectangle &&
                        node.getUserData() != null &&
                        node.getUserData().equals("ghost")
        );

        if (ghost != null) {
            for (int[] coord : ghost) {
                int x = coord[1];
                int y = coord[0];

                // creating transparent ghost block
                Rectangle ghostBlock = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                ghostBlock.setFill(Color.color(1, 1, 1, 0.2)); // white, 20% opacity
                ghostBlock.setStroke(Color.GRAY);
                ghostBlock.setStrokeWidth(1);
                ghostBlock.setUserData("ghost");
                ghostBlock.setMouseTransparent(true);

                // place in the main grid using GridPane coordinates (must be direct child of GridPane)
                GridPane.setColumnIndex(ghostBlock, x);
                GridPane.setRowIndex(ghostBlock, y - 2); // adjust for hidden top rows

                // add directly to the gamePanel so GridPane constraints apply
                gamePanel.getChildren().add(ghostBlock);
            }
        }

        // keep notifications layer above everything, unless the game is over
        if (!isGameOver.getValue()) {
            groupNotification.toFront();
        }

    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    /**
     *
     * @param event added logic for Points screen that got messed up while I was adding ghost shadow logic
     */
    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData != null) {
                if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                    NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                    // center within the gamePanel
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
                refreshBrick(downData.getViewData());
            }
        }
        gamePanel.requestFocus();
        if (!isGameOver.getValue()) {
            groupNotification.toFront();
        }
    }

    // method for moving hard down, using key SPACE
    private void moveDownHard(MoveEvent moveEvent) {
        DownData dd = eventListener.onHardDropEvent(moveEvent);
        if (dd != null) {
            refreshBrick(dd.getViewData());
            if (dd.getClearRow() != null && dd.getClearRow().getLinesRemoved() > 0) {
                // using the board matrix from the controller via eventListener
                refreshGameBackground(((GameController) eventListener).getBoardMatrix());
            }
        }
        if (!isGameOver.getValue()) {
            groupNotification.toFront();
        }
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Displays a temporary popup below the score label (e.g., "+50").
     * Implemented my own since the default code was being buggy due to the ghost shadow implementation
     */
    public void showScoreBonus(int bonus) {
        Text popup = new Text("+" + bonus);
        popup.setFont(Font.font("Verdana", 22));
        popup.setFill(Color.WHITESMOKE);

        // Position slightly below the score label
        double startX = scoreLabel.getLayoutX() + 10;
        double startY = scoreLabel.getLayoutY() + 30;
        popup.setLayoutX(startX);
        popup.setLayoutY(startY);

        Pane parent = (Pane) scoreLabel.getParent();
        parent.getChildren().add(popup);

        // Move upward slightly
        TranslateTransition moveUp = new TranslateTransition(Duration.millis(1000), popup);
        moveUp.setByY(-20); // move up 20px

        // Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), popup);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // Play both animations together
        moveUp.play();
        fadeOut.play();

        // Remove after fade
        fadeOut.setOnFinished(e -> parent.getChildren().remove(popup));
    }

    public void bindScore(IntegerProperty integerProperty) {
        // Unbind previous binding if any then bind to the provided score property
        if (scoreLabel.textProperty().isBound()) {
            scoreLabel.textProperty().unbind();
        }
        scoreLabel.textProperty().bind(integerProperty.asString());
    }

    public void gameOver() {
        timeLine.stop();
        // remove only transient notifications; keep the GameOverPanel container inside groupNotification
        groupNotification.getChildren().removeIf(node -> node instanceof NotificationPanel);
        gamePanel.setOpacity(0.6);
        // show `game over` panel above everything
        gameOverPanel.setVisible(true);
        gameOverPanel.toFront();
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void togglePause() {
        if (isPause.getValue() == Boolean.TRUE) {
            // resumes the game
            timeLine.play();
            isPause.setValue(Boolean.FALSE);
            groupNotification.getChildren().removeIf(node -> node instanceof NotificationPanel); // remove only transient notification panels
            gamePanel.setOpacity(1.0); // restore full opacity when resuming
        } else {
            // pauses the game
            timeLine.pause();
            isPause.setValue(Boolean.TRUE);
            NotificationPanel pausedMsg = new NotificationPanel("PAUSED");
            groupNotification.getChildren().add(pausedMsg);
            pausedMsg.showScore(groupNotification.getChildren());
            gamePanel.setOpacity(isPause.getValue() ? 0.6 : 1.0); // reduces opacity of the board when paused
        }
        gamePanel.requestFocus();
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}