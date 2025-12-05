package com.comp2042.game;

/**
 * DifficultyManager - Manages game difficulty levels and their associated game
 * speeds.
 * Handles the mapping between difficulty levels (1, 5, 10, 15) and brick fall
 * speeds.
 * Level 1 is the easiest (the slowest fall), and Level 15 is the hardest (the
 * fastest
 * fall).
 * Responsibilities:
 * - Store and retrieve current difficulty level
 * - Calculate brick fall delay based on difficulty level
 * - Provide difficulty descriptions for UI display
 * 
 * @author Umer Imran
 * @version 1.0
 */
public class DifficultyManager {

    private int currentLevel;

    // Difficulty level constants
    public static final int LEVEL_1 = 1;
    public static final int LEVEL_5 = 5;
    public static final int LEVEL_10 = 10;
    public static final int LEVEL_15 = 15;

    public DifficultyManager() {
        this.currentLevel = LEVEL_1; // Default to level 1 (easiest)
    }

    public DifficultyManager(int initialLevel) {
        setLevel(initialLevel);
    }

    /**
     * Sets the current difficulty level.
     * Valid levels: 1, 5, 10, 15
     * 
     * @param level the difficulty level to set
     * @throws IllegalArgumentException if a level is not a valid difficulty level
     */
    public void setLevel(int level) {
        if (level == LEVEL_1 || level == LEVEL_5 || level == LEVEL_10 || level == LEVEL_15) {
            this.currentLevel = level;
        } else {
            throw new IllegalArgumentException("Invalid difficulty level: " + level +
                    ". Valid levels are: 1, 5, 10, 15");
        }
    }

    /**
     * Gets the current difficulty level.
     * 
     * @return the current difficulty level (1, 5, 10, or 15)
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Gets the brick fall delay in milliseconds for the current difficulty level.
     * Lower delay = faster falling = harder difficulty.
     * Level 1: 1000 ms (Normal)
     * Level 5: 700 ms (Medium)
     * Level 10: 400 ms (Hard)
     * Level 15: 150 ms (Very Hard)
     * 
     * @return the delay in milliseconds between brick falls
     */
    public int getDelayForCurrentLevel() {
        return getDelayForLevel(currentLevel);
    }

    /**
     * Gets the brick fall delay for a specific difficulty level.
     * 
     * @param level the difficulty level (1, 5, 10, or 15)
     * @return the delay in milliseconds between brick falls
     */
    public static int getDelayForLevel(int level) {
        return switch (level) {
            case LEVEL_1 -> 1000; // Normal speed
            case LEVEL_5 -> 700; // Medium speed
            case LEVEL_10 -> 400; // Hard speed
            case LEVEL_15 -> 150; // Very hard speed
            default ->
                // Fallback formula for levels outside the predefined range
                Math.max(100, 1000 - (level * 50));
        };
    }

    /**
     * Gets a descriptive label for the current difficulty level.
     * 
     * @return a string describing the difficulty (e.g., "LEVEL 1 - NORMAL")
     */
    public String getCurrentLevelDescription() {
        return getLevelDescription(currentLevel);
    }

    /**
     * Gets a descriptive label for a specific difficulty level.
     * 
     * @param level the difficulty level
     * @return a string describing the difficulty
     */
    public static String getLevelDescription(int level) {
        return switch (level) {
            case LEVEL_1 -> "LEVEL 1 - NORMAL";
            case LEVEL_5 -> "LEVEL 5 - MEDIUM";
            case LEVEL_10 -> "LEVEL 10 - HARD";
            case LEVEL_15 -> "LEVEL 15 - EXTREME";
            default -> "LEVEL " + level;
        };
    }

    /**
     * Cycles to the next difficulty level.
     * Order: 1 → 5 → 10 → 15 → 1 (cycles back)
     * 
     * @return the new difficulty level
     */
    public int nextLevel() {
        switch (currentLevel) {
            case LEVEL_1:
                setLevel(LEVEL_5);
                break;
            case LEVEL_5:
                setLevel(LEVEL_10);
                break;
            case LEVEL_10:
                setLevel(LEVEL_15);
                break;
            case LEVEL_15:
                setLevel(LEVEL_1);
                break;
        }
        return currentLevel;
    }

    /**
     * Gets the next difficulty level without changing the current one.
     * 
     * @return the next level in the cycle
     */
    public int getNextLevel() {
        return switch (currentLevel) {
            case LEVEL_1 -> LEVEL_5;
            case LEVEL_5 -> LEVEL_10;
            case LEVEL_10 -> LEVEL_15;
            default -> LEVEL_1;
        };
    }
}
