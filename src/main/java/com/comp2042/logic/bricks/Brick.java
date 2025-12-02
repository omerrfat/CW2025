package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Brick - Interface defining the contract for Tetris brick pieces.
 * 
 * Each brick implementation must provide:
 * - A list of rotation states (each state is a 2D matrix representation)
 * 
 * Standard Tetris pieces have 1-4 rotation states depending on symmetry.
 * 
 * @author Umer Imran
 * @version 2.0
 */
public interface Brick {

    List<int[][]> getShapeMatrix();
}
