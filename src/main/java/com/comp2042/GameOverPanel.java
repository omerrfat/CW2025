package com.comp2042;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;

public class GameOverPanel extends BorderPane {

    private Label gameOverLabel;

    public GameOverPanel() {
        gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        setCenter(gameOverLabel);
    }

    /**
     * Animating the GAME OVER text with a pulse/flash effect
     */
    public void playGameOverAnimation() {
        Timeline flashTimeline = new Timeline();

        // Flash the text color and scale
        flashTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(gameOverLabel.scaleXProperty(), 1.0),
                        new KeyValue(gameOverLabel.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.millis(100),
                        new KeyValue(gameOverLabel.scaleXProperty(), 1.15),
                        new KeyValue(gameOverLabel.scaleYProperty(), 1.15)),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(gameOverLabel.scaleXProperty(), 0.95),
                        new KeyValue(gameOverLabel.scaleYProperty(), 0.95)),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(gameOverLabel.scaleXProperty(), 1.12),
                        new KeyValue(gameOverLabel.scaleYProperty(), 1.12)),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(gameOverLabel.scaleXProperty(), 1.0),
                        new KeyValue(gameOverLabel.scaleYProperty(), 1.0)));

        flashTimeline.play();
    }

}
