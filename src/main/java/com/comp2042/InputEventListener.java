package com.comp2042;

/**
 * InputEventListener - Interface for handling game input events.
 * 
 * Implemented by GameController to process player moves.
 * 
 * Events:
 * - onDownEvent: Brick moves down (gravity or manual)
 * - onLeftEvent: Brick moves left
 * - onRightEvent: Brick moves right
 * - onRotateEvent: Brick rotates clockwise
 * - onHardDropEvent: Brick instantly falls to bottom
 * - onHoldEvent: Swap active brick with held brick
 * - createNewGame: Start a new game session
 * 
 * @author Umer Imran
 * @version 2.0
 */
public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);

    // New method for hard dropping of the blocks
    DownData onHardDropEvent(MoveEvent event);

    ViewData onHoldEvent(MoveEvent event);

    void createNewGame();

}
