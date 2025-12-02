package com.comp2042.game;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * GameStateManager - Manages game state with Observer pattern.
 * 
 * Single Responsibility: Manage and notify about game state changes.
 * 
 * Design Pattern: Observer Pattern
 * - Observers can subscribe to state changes (pause, game over, etc.)
 * - Decouples game logic from UI components
 * - Multiple listeners can react to state changes independently
 * 
 * Responsibilities:
 * - Maintain pause/game over/game started states
 * - Notify listeners of state transitions
 * - Provide observable properties for UI binding
 * 
 * This replaces scattered BooleanProperty fields in GuiController.
 * 
 * @author Umer Imran
 * @version 2.0
 */
public class GameStateManager {

    // Observable properties that UI components can bind to
    private final BooleanProperty isPausedProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty isGameOverProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty isRunningProperty = new SimpleBooleanProperty(false);

    private GameStateListener gameStateListener;

    /**
     * Sets the listener for game state changes.
     * Observer Pattern: Register to be notified of state changes.
     * 
     * @param listener The GameStateListener implementation
     */
    public void setGameStateListener(GameStateListener listener) {
        this.gameStateListener = listener;
    }

    /**
     * Toggles pause state and notifies listeners.
     */
    public void togglePause() {
        boolean newState = !isPausedProperty.get();
        setPaused(newState);
    }

    /**
     * Sets the pause state.
     * 
     * @param paused true to pause, false to resume
     */
    public void setPaused(boolean paused) {
        isPausedProperty.set(paused);
        if (gameStateListener != null) {
            if (paused) {
                gameStateListener.onGamePaused();
            } else {
                gameStateListener.onGameResumed();
            }
        }
    }

    /**
     * Ends the game (game over state).
     */
    public void setGameOver() {
        isGameOverProperty.set(true);
        isRunningProperty.set(false);
        if (gameStateListener != null) {
            gameStateListener.onGameOver();
        }
    }

    /**
     * Starts a new game.
     */
    public void startGame() {
        isPausedProperty.set(false);
        isGameOverProperty.set(false);
        isRunningProperty.set(true);
        if (gameStateListener != null) {
            gameStateListener.onGameStarted();
        }
    }

    /**
     * Resets game to initial state.
     */
    public void reset() {
        isPausedProperty.set(false);
        isGameOverProperty.set(false);
        isRunningProperty.set(false);
    }

    // ========== STATE GETTERS ==========

    public boolean isPaused() {
        return isPausedProperty.get();
    }

    public boolean isGameOver() {
        return isGameOverProperty.get();
    }

    public boolean isRunning() {
        return isRunningProperty.get();
    }

    // ========== OBSERVABLE PROPERTIES FOR UI BINDING ==========

    public BooleanProperty pausedProperty() {
        return isPausedProperty;
    }

    public BooleanProperty gameOverProperty() {
        return isGameOverProperty;
    }

    public BooleanProperty runningProperty() {
        return isRunningProperty;
    }

    /**
     * Observer interface for game state changes.
     * Allows multiple listeners to react to state transitions.
     */
    public interface GameStateListener {
        void onGamePaused();

        void onGameResumed();

        void onGameOver();

        void onGameStarted();
    }
}
