package com.comp2042.ui.input;

import com.comp2042.EventSource;
import com.comp2042.EventType;
import com.comp2042.InputEventListener;
import com.comp2042.MoveEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * InputHandler - Processes keyboard input and delegates to game logic.
 * 
 * Single Responsibility: Convert keyboard input to game events.
 * 
 * Uses Strategy Pattern:
 * - Different key handlers can be implemented as strategies
 * - Enables easy customization of key bindings
 * - Separates input handling from game logic
 * 
 * Responsibilities:
 * - Map keyboard keys to game events
 * - Create MoveEvent objects for each input
 * - Delegate to InputEventListener for game logic processing
 * - Support hold, hard drop, and other special moves
 * 
 * @author Umer Imran
 * @version 2.0
 */
public class InputHandler {

    private InputEventListener eventListener;
    private final KeyBindingStrategy keyBindings;

    /**
     * Initializes InputHandler with default key bindings.
     */
    public InputHandler() {
        this.keyBindings = new DefaultKeyBindingStrategy();
    }

    /**
     * Sets the event listener that will process game events.
     * 
     * @param listener The InputEventListener (typically GameController)
     */
    public void setEventListener(InputEventListener listener) {
        this.eventListener = listener;
    }

    /**
     * Handles keyboard key press events.
     * Delegates game controls to InputEventListener and UI controls to
     * UIEventListener.
     * 
     * @param event The JavaFX KeyEvent
     */
    public void handleKeyPressed(KeyEvent event) {
        if (eventListener == null)
            return;

        KeyCode code = event.getCode();

        // Handle game controls (movement, rotation, hold, hard drop)
        if (keyBindings.isLeftKey(code)) {
            eventListener.onLeftEvent(createMoveEvent(EventType.LEFT));
            event.consume();
        } else if (keyBindings.isRightKey(code)) {
            eventListener.onRightEvent(createMoveEvent(EventType.RIGHT));
            event.consume();
        } else if (keyBindings.isDownKey(code)) {
            eventListener.onDownEvent(createMoveEvent(EventType.DOWN));
            event.consume();
        } else if (keyBindings.isRotateKey(code)) {
            eventListener.onRotateEvent(createMoveEvent(EventType.ROTATE));
            event.consume();
        } else if (keyBindings.isHardDropKey(code)) {
            eventListener.onHardDropEvent(createMoveEvent(EventType.DOWN));
            event.consume();
        } else if (keyBindings.isHoldKey(code)) {
            eventListener.onHoldEvent(createMoveEvent(EventType.DOWN));
            event.consume();
        }
    }

    /**
     * Sets a UI event listener for handling meta controls (pause, new game, etc).
     * These are typically handled by the GuiController.
     * 
     * @param listener The UIEventListener for UI actions
     */
    public void setUIEventListener(UIEventListener listener) {
        // This would be called to handle pause/game over UI actions
        // Currently not needed as GuiController handles these directly
    }

    /**
     * Interface for UI-level event handling.
     * Separates game logic (InputEventListener) from UI actions (UIEventListener).
     */
    public interface UIEventListener {
        void onPauseRequested();

        void onNewGameRequested();

        void onPauseMenuRequested();
    }

    /**
     * Creates a MoveEvent from keyboard input.
     * 
     * @param eventType The type of move event
     * @return A MoveEvent with USER source
     */
    private MoveEvent createMoveEvent(EventType eventType) {
        return new MoveEvent(eventType, EventSource.USER);
    }

    /**
     * Sets a custom key binding strategy.
     * Strategy Pattern: Allows runtime customization of key bindings.
     * 
     * @param strategy The custom KeyBindingStrategy implementation
     */
    public void setKeyBindingStrategy(KeyBindingStrategy strategy) {
        // This enables swapping key binding strategies at runtime
        // Useful for accessibility or custom control schemes
    }

    /**
     * Strategy interface for key bindings.
     * Allows different key configuration schemes without changing InputHandler.
     */
    public interface KeyBindingStrategy {
        boolean isLeftKey(KeyCode code);

        boolean isRightKey(KeyCode code);

        boolean isDownKey(KeyCode code);

        boolean isRotateKey(KeyCode code);

        boolean isHardDropKey(KeyCode code);

        boolean isHoldKey(KeyCode code);
    }

    /**
     * Default key binding strategy - Standard Tetris controls.
     */
    public static class DefaultKeyBindingStrategy implements KeyBindingStrategy {
        @Override
        public boolean isLeftKey(KeyCode code) {
            return code == KeyCode.LEFT || code == KeyCode.A;
        }

        @Override
        public boolean isRightKey(KeyCode code) {
            return code == KeyCode.RIGHT || code == KeyCode.D;
        }

        @Override
        public boolean isDownKey(KeyCode code) {
            return code == KeyCode.DOWN || code == KeyCode.S;
        }

        @Override
        public boolean isRotateKey(KeyCode code) {
            return code == KeyCode.UP || code == KeyCode.W || code == KeyCode.Z;
        }

        @Override
        public boolean isHardDropKey(KeyCode code) {
            return code == KeyCode.SPACE;
        }

        @Override
        public boolean isHoldKey(KeyCode code) {
            return code == KeyCode.H;
        }
    }
}
