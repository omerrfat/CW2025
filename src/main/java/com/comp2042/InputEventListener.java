package com.comp2042;

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
