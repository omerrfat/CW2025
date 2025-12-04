package com.comp2042.ui;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * AnimationManager - Centralizes all animation logic in the game.
 * 
 * Single Responsibility: Create and manage all game animations.
 * 
 * Responsibilities:
 * - Line clear animations (fade and scaling effects)
 * - Screen shake effects (game over impact)
 * - Game over pulse animations
 * - Pause menu slide-in animations
 * - Score notification animations
 * 
 * This class isolates animation complexity from the main GuiController.
 * 
 * @author Umer Imran
 * @version 2.0
 */
public class AnimationManager {

    private static final int SHAKE_DURATION_MS = 50;
    private static final int SHAKE_COUNT = 8;
    private static final double SHAKE_OFFSET = 5.0;

    /**
     * Creates a screen shake animation for dramatic game over effect.
     * 
     * @param target The GridPane to shake
     * @return A Timeline that performs the shake animation
     */
    public Timeline createScreenShake(GridPane target) {
        Timeline timeline = new Timeline();

        for (int i = 0; i < SHAKE_COUNT; i++) {
            double translateX = (i % 2 == 0) ? SHAKE_OFFSET : -SHAKE_OFFSET;
            KeyFrame frame = new KeyFrame(
                    Duration.millis(i * SHAKE_DURATION_MS),
                    new KeyValue(target.translateXProperty(), translateX));
            timeline.getKeyFrames().add(frame);
        }

        // Reset position at end
        KeyFrame resetFrame = new KeyFrame(
                Duration.millis(SHAKE_COUNT * SHAKE_DURATION_MS),
                new KeyValue(target.translateXProperty(), 0));
        timeline.getKeyFrames().add(resetFrame);

        return timeline;
    }

    /**
     * Creates a line clear animation - rows fade out and shrink.
     * 
     * @param rows       The rows to animate
     * @param duration   The animation duration
     * @param onFinished Callback when animation completes
     * @return A ParallelTransition combining fade and scale effects
     */
    public ParallelTransition createLineClearAnimation(GridPane[] rows, Duration duration, Runnable onFinished) {
        ParallelTransition parallelTransition = new ParallelTransition();

        for (GridPane row : rows) {
            FadeTransition fade = new FadeTransition(duration, row);
            fade.setToValue(0);

            ScaleTransition scale = new ScaleTransition(duration, row);
            scale.setToY(0);

            parallelTransition.getChildren().addAll(fade, scale);
        }

        parallelTransition.setOnFinished(e -> onFinished.run());
        return parallelTransition;
    }

    /**
     * Creates a game over text pulse animation.
     * 
     * @param target The Text to animate
     * @return A Timeline with scale pulse effect
     */
    public Timeline createPulseAnimation(Text target) {
        Timeline timeline = new Timeline();

        // Pulse sequence: 1.0 → 1.15 → 0.95 → 1.12 → 1.0
        double[] scales = { 1.0, 1.15, 0.95, 1.12, 1.0 };
        int duration = 100;

        for (int i = 0; i < scales.length; i++) {
            KeyFrame frame = new KeyFrame(
                    Duration.millis(i * duration),
                    new KeyValue(target.scaleXProperty(), scales[i]),
                    new KeyValue(target.scaleYProperty(), scales[i]));
            timeline.getKeyFrames().add(frame);
        }

        return timeline;
    }

    /**
     * Creates a pause menu slide-in animation.
     * 
     * @param pausePanel The pane to animate
     * @return A ParallelTransition with scale and fade effects
     */
    public ParallelTransition createPauseMenuAnimation(Pane pausePanel) {
        // Scale from 0.8 to 1.0
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(400), pausePanel);
        scaleTransition.setFromX(0.8);
        scaleTransition.setFromY(0.8);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        // Fade in from 0.5 to 1.0
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), pausePanel);
        fadeTransition.setFromValue(0.5);
        fadeTransition.setToValue(1.0);

        ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, fadeTransition);
        return parallelTransition;
    }

    /**
     * Creates a score bonus notification animation.
     * 
     * @param notificationNode The node to animate
     * @return A ParallelTransition with fade and translate effects
     */
    public ParallelTransition createScoreNotificationAnimation(Pane notificationNode) {
        // Move upward
        TranslateTransition moveUp = new TranslateTransition(Duration.millis(1500), notificationNode);
        moveUp.setByY(-50);

        // Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(1500), notificationNode);
        fadeOut.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(moveUp, fadeOut);
        return parallelTransition;
    }

    /**
     * Creates a subtle border fade animation for line clears.
     * 
     * @param borderTarget The node whose border to animate
     * @return A Timeline with subtle glow fade effect
     */
    public Timeline createBorderGlowAnimation(javafx.scene.layout.BorderPane borderTarget) {
        Timeline timeline = new Timeline();

        // create a subtle glow effect that appears and fades
        javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
        glow.setColor(javafx.scene.paint.Color.RED);
        glow.setSpread(0.5);

        // grow glow radius smoothly
        KeyFrame frame1 = new KeyFrame(
                Duration.millis(0),
                new KeyValue(glow.radiusProperty(), 0));
        KeyFrame frame2 = new KeyFrame(
                Duration.millis(200),
                new KeyValue(glow.radiusProperty(), 8));
        // shrink glow radius smoothly
        KeyFrame frame3 = new KeyFrame(
                Duration.millis(600),
                new KeyValue(glow.radiusProperty(), 0));

        timeline.getKeyFrames().addAll(frame1, frame2, frame3);

        timeline.setOnFinished(e -> borderTarget.setEffect(null));
        borderTarget.setEffect(glow);

        return timeline;
    }
}
