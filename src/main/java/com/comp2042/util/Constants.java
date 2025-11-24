package com.comp2042.util;

import javafx.scene.paint.Color;

/**
 * Central configuration for all game constants.
 * Eliminates magic numbers and makes configuration changes easier.
 */
public final class Constants {

    // Prevent instantiation
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }

    // === BOARD DIMENSIONS ===
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 25;
    public static final int VISIBLE_ROWS = 20;  // Rows visible to player
    public static final int HIDDEN_ROWS = 5;     // Buffer rows at top

    // === RENDERING ===
    public static final int BRICK_SIZE = 20;
    public static final int GRID_GAP = 1;
    public static final int BRICK_ARC_SIZE = 9;

    // === PREVIEW BOXES ===
    public static final int PREVIEW_BOX_1_SIZE = 100;
    public static final int PREVIEW_BOX_2_SIZE = 80;
    public static final int PREVIEW_BOX_3_SIZE = 80;
    public static final int PREVIEW_BRICK_SIZE_SMALL = 15;  // For preview 2 and 3
    public static final int NUM_PREVIEW_PIECES = 3;

    // === GAME TIMING ===
    public static final int INITIAL_FALL_SPEED_MS = 400;
    public static final int SCORE_POPUP_DURATION_MS = 1000;
    public static final int NOTIFICATION_FADE_MS = 1000;

    // === SCORING ===
    public static final int SOFT_DROP_POINTS = 1;
    public static final int HARD_DROP_MULTIPLIER = 2;
    public static final int[] LINE_CLEAR_POINTS = {0, 100, 300, 500, 800}; // 0, 1, 2, 3, 4 lines

    // === UI LAYOUT ===
    public static final int GAME_PANEL_X = 40;
    public static final int GAME_PANEL_Y = 30;
    public static final int BRICK_PANEL_Y_OFFSET = -42;
    public static final int SCORE_PANEL_X = 300;
    public static final int SCORE_PANEL_Y = 32;
    public static final int SCORE_POPUP_OFFSET_X = 310;
    public static final int SCORE_POPUP_OFFSET_Y = 95;

    // === PIECE COLORS ===
    public static final class PieceColors {
        public static final Color TRANSPARENT = Color.TRANSPARENT;
        public static final Color I_PIECE = Color.AQUA;           // Cyan I-piece
        public static final Color J_PIECE = Color.BLUEVIOLET;     // Blue J-piece
        public static final Color L_PIECE = Color.DARKGREEN;      // Orange L-piece
        public static final Color O_PIECE = Color.YELLOW;         // Yellow square
        public static final Color S_PIECE = Color.RED;            // Green S-piece
        public static final Color T_PIECE = Color.BEIGE;          // Purple T-piece
        public static final Color Z_PIECE = Color.BURLYWOOD;      // Red Z-piece

        public static Color getColor(int colorCode) {
            switch (colorCode) {
                case 0: return TRANSPARENT;
                case 1: return I_PIECE;
                case 2: return J_PIECE;
                case 3: return L_PIECE;
                case 4: return O_PIECE;
                case 5: return S_PIECE;
                case 6: return T_PIECE;
                case 7: return Z_PIECE;
                default: return TRANSPARENT;
            }
        }
    }

    // === GHOST PIECE ===
    public static final Color GHOST_FILL = Color.color(1, 1, 1, 0.2);  // White 20% opacity
    public static final Color GHOST_STROKE = Color.GRAY;
    public static final double GHOST_STROKE_WIDTH = 1.0;

    // === GAME STATES ===
    public static final double PAUSED_OPACITY = 0.6;
    public static final double ACTIVE_OPACITY = 1.0;

    // === STARTING POSITION ===
    public static final int SPAWN_X = 4;
    public static final int SPAWN_Y = 0;

    // === ANIMATION ===
    public static final int SCORE_POPUP_MOVE_Y = -20;  // pixels to move up
}