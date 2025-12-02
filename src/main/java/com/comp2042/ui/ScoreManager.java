package com.comp2042.ui;

import javafx.beans.property.IntegerProperty;
import javafx.scene.control.Label;

/**
 * ScoreManager - Manages score display and high score tracking.
 * 
 * Single Responsibility: Handle score updates and display.
 * 
 * Responsibilities:
 * - Bind score to UI label
 * - Track and update high score
 * - Format score for display
 * - Persist high score state
 * 
 * This separates score management from general UI control.
 * 
 * @author Umer Imran
 * @version 2.0
 */
public class ScoreManager {

    private final Label scoreLabel;
    private final Label highScoreLabel;
    private int highScore = 0;

    /**
     * Initializes the ScoreManager with UI labels.
     * 
     * @param scoreLabel     Label for current score display
     * @param highScoreLabel Label for high score display
     */
    public ScoreManager(Label scoreLabel, Label highScoreLabel) {
        this.scoreLabel = scoreLabel;
        this.highScoreLabel = highScoreLabel;
        updateHighScoreDisplay();
    }

    /**
     * Binds the score property to the score label for real-time updates.
     * 
     * @param scoreProperty The observable score property from the game
     */
    public void bindScore(IntegerProperty scoreProperty) {
        if (scoreLabel.textProperty().isBound()) {
            scoreLabel.textProperty().unbind();
        }
        scoreLabel.textProperty().bind(scoreProperty.asString());
    }

    /**
     * Updates the high score if the current score exceeds it.
     * 
     * @param currentScore The current game score
     */
    public void updateHighScore(int currentScore) {
        if (currentScore > highScore) {
            highScore = currentScore;
            updateHighScoreDisplay();
        }
    }

    /**
     * Gets the current high score value.
     * 
     * @return The high score
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Sets the high score to a specific value (useful for loading saved scores).
     * 
     * @param score The high score to set
     */
    public void setHighScore(int score) {
        this.highScore = score;
        updateHighScoreDisplay();
    }

    /**
     * Updates the high score display label.
     */
    private void updateHighScoreDisplay() {
        highScoreLabel.setText(String.format("High Score: %d", highScore));
    }
}
