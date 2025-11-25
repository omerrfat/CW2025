package com.comp2042.logic.bricks;

public interface BrickGenerator {

    Brick getBrick();

    Brick getNextBrick();

    /**
     * get the next 3 bricks that will fall
     */
    Brick[] getNextThreeBricks();

    /**
     * Peek at the next N bricks from the generator without removing them.
     * Implementations should guarantee the returned array contains `count`
     * non-null brick objects whenever possible (filling from the available
     * brick pool).
     */
    Brick[] peekNext(int count);
}
