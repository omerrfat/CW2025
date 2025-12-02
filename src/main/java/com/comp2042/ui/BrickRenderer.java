package com.comp2042.ui;

import com.comp2042.util.Constants;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * BrickRenderer - Handles all brick rendering on the game board.
 * 
 * Single Responsibility: Render bricks with proper colors and positioning.
 * 
 * Responsibilities:
 * - Create and manage brick rectangles
 * - Apply correct colors based on brick type
 * - Render bricks on game board and preview panes
 * - Handle brick visibility and updates
 * 
 * @author Umer Imran
 * @version 2.0
 */
public class BrickRenderer {

    private final ColorProvider colorProvider;

    public BrickRenderer() {
        this.colorProvider = new ColorProvider();
    }

    /**
     * Creates a rectangle for a single brick cell.
     * 
     * @param colorCode The color code for the brick type
     * @return A styled Rectangle for the brick cell
     */
    public Rectangle createBrickRectangle(int colorCode) {
        Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
        rectangle.setFill(colorProvider.getColor(colorCode));
        rectangle.setArcHeight(Constants.BRICK_ARC_SIZE);
        rectangle.setArcWidth(Constants.BRICK_ARC_SIZE);
        return rectangle;
    }

    /**
     * Updates a brick rectangle's color.
     * 
     * @param colorCode The new color code
     * @param rectangle The rectangle to update
     */
    public void updateBrickColor(int colorCode, Rectangle rectangle) {
        rectangle.setFill(colorProvider.getColor(colorCode));
    }

    /**
     * Clears all rectangles from a grid pane.
     * 
     * @param gridPane The pane to clear
     */
    public void clearPane(GridPane gridPane) {
        gridPane.getChildren().clear();
    }

    /**
     * Renders a brick on a grid pane at the specified position.
     * 
     * @param gridPane  The target grid pane
     * @param brickData The brick matrix data
     * @param startCol  Starting column for positioning
     * @param startRow  Starting row for positioning
     */
    public void renderBrick(GridPane gridPane, int[][] brickData, int startCol, int startRow) {
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    Rectangle rect = createBrickRectangle(brickData[i][j]);
                    gridPane.add(rect, j + startCol, i + startRow);
                }
            }
        }
    }

    /**
     * Get the color provider for direct access if needed.
     * 
     * @return The ColorProvider instance
     */
    public ColorProvider getColorProvider() {
        return colorProvider;
    }

    /**
     * Inner class for color management - separates color logic from rendering.
     */
    public static class ColorProvider {
        private static final int EMPTY = 0;

        /**
         * Gets the fill color for a given brick type code.
         * 
         * @param colorCode The brick type code
         * @return The Color to apply
         */
        public Color getColor(int colorCode) {
            if (colorCode == EMPTY) {
                return Color.TRANSPARENT;
            }
            return Constants.PieceColors.getColor(colorCode);
        }
    }
}
