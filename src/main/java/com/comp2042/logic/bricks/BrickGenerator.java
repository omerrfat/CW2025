package com.comp2042.logic.bricks;

/**
 * BrickGenerator - Interface for generating Tetris brick pieces.
 * 
 * Responsibilities:
 * - Provide current and next brick in sequence
 * - Allow lookahead preview of upcoming bricks (3-brick preview)
 * - Manage brick generation strategy (random, sequence, etc.)
 * 
 * Allows game to display "Next" preview and player planning.
 * 
 * @author Umer Imran
 * @version 2.0
 */
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
