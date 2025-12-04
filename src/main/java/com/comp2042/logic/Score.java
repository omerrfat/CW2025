package com.comp2042.logic;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Score - Manages the player's current score using JavaFX property binding.
 * 
 * Responsibilities:
 * - Maintains current score value as observable property
 * - Provides score addition and reset functionality
 * - Enables real-time UI updates via property binding
 * 
 * Scoring: Points awarded for line clears and other actions
 * 
 * @author Umer Imran
 * @version 2.0
 */
public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);

    public IntegerProperty scoreProperty() {
        return score;
    }

    public void add(int i) {
        score.setValue(score.getValue() + i);
    }

    public void reset() {
        score.setValue(0);
    }

}
