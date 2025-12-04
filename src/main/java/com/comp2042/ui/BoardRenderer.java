package com.comp2042.ui;

import com.comp2042.util.Constants;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * BoardRenderer - Renders the static game board background.
 * 
 * Single Responsibility: Initialize and maintain the game board visual
 * representation.
 * 
 * Responsibilities:
 * - Create the 25x10 game board grid
 * - Render board background cells
 * - Apply consistent board styling
 * - Manage board dimensions and layout
 * 
 * This separates board initialization from dynamic brick rendering.
 * Works with BrickRenderer for complete board visualization.
 * 
 * @author Umer Imran
 * @version 2.0
 */
public class BoardRenderer {

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 25;
    private static final Color BOARD_BACKGROUND = Color.web("#1a1a1a");
    private static final Color CELL_BORDER = Color.web("#333333");

    /**
     * Initializes the game board with background cells.
     * Creates a 25x10 grid with styled cells.
     * 
     * @param boardPane The GridPane to initialize as the game board
     * @return A 2D array of Rectangles representing the board cells
     */
    public Rectangle[][] initializeBoard(GridPane boardPane) {
        Rectangle[][] displayMatrix = new Rectangle[BOARD_HEIGHT][BOARD_WIDTH];

        // Set board styling
        boardPane.setStyle("-fx-border-color: #666666; -fx-border-width: 2; -fx-gap: 0;");
        boardPane.setPrefWidth(Constants.BRICK_SIZE * BOARD_WIDTH);
        boardPane.setPrefHeight(Constants.BRICK_SIZE * BOARD_HEIGHT);

        // Create background cells
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Rectangle cell = createBackgroundCell();
                displayMatrix[i][j] = cell;
                boardPane.add(cell, j, i);
            }
        }

        return displayMatrix;
    }

    /**
     * Creates a single background cell for the board.
     * 
     * @return A styled Rectangle for a board cell
     */
    private Rectangle createBackgroundCell() {
        Rectangle cell = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
        cell.setFill(BOARD_BACKGROUND);
        cell.setStroke(CELL_BORDER);
        cell.setStrokeWidth(0.5);
        cell.setArcHeight(2);
        cell.setArcWidth(2);
        return cell;
    }

    /**
     * Gets board dimensions.
     * 
     * @return An int array with [width, height]
     */
    public int[] getBoardDimensions() {
        return new int[] { BOARD_WIDTH, BOARD_HEIGHT };
    }

    /**
     * Updates a specific board cell color.
     * Used when bricks lock into place on the board.
     * 
     * @param row           The row index
     * @param col           The column index
     * @param colorCode     The color code for the brick
     * @param displayMatrix The board matrix
     */
    public void updateBoardCell(int row, int col, int colorCode, Rectangle[][] displayMatrix) {
        if (row < BOARD_HEIGHT && col < BOARD_WIDTH && colorCode != 0) {
            Color color = Constants.PieceColors.getColor(colorCode);
            displayMatrix[row][col].setFill(color);
        }
    }

    /**
     * Clears a row on the board (used for line clear visualization).
     * 
     * @param row           The row index to clear
     * @param displayMatrix The board matrix
     */
    public void clearBoardRow(int row, Rectangle[][] displayMatrix) {
        if (row < BOARD_HEIGHT) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                displayMatrix[row][col].setFill(BOARD_BACKGROUND);
            }
        }
    }
}
