package com.comp2042.logic.bricks;

public interface BrickGenerator {

    Brick getBrick();

    Brick getNextBrick();

    /**
     * Get the next 3 bricks that will fall
     */
    Brick[] getNextThreeBricks();
}
