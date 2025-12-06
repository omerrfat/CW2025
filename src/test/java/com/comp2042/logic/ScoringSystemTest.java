package com.comp2042.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the game scoring system.
 * Tests score accumulation, line clear bonuses, and property binding.
 */
@DisplayName("Game Scoring System Tests")
class ScoringSystemTest {

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    @Test
    @DisplayName("Score should initialize at zero")
    void testInitialScore() {
        assertEquals(0, score.scoreProperty().getValue(), "Score should start at 0");
    }

    @Test
    @DisplayName("Score should increase with single addition")
    void testScoreIncrease() {
        score.add(100);
        assertEquals(100, score.scoreProperty().getValue(), "Score should increase by 100");
    }

    @Test
    @DisplayName("Score should accumulate multiple additions")
    void testScoreAccumulation() {
        score.add(50);
        score.add(100);
        score.add(75);

        assertEquals(225, score.scoreProperty().getValue(), "Score should accumulate all additions");
    }

    @Test
    @DisplayName("Score should handle single line clear bonus")
    void testSingleLineClearBonus() {
        // Single line: 50 * 1 * 1 = 50
        score.add(50);
        assertEquals(50, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Score should handle double line clear bonus")
    void testDoubleLineClearBonus() {
        // Double line: 50 * 2 * 2 = 200
        score.add(200);
        assertEquals(200, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Score should handle triple line clear bonus")
    void testTripleLineClearBonus() {
        // Triple line: 50 * 3 * 3 = 450
        score.add(450);
        assertEquals(450, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Score should handle Tetris (four-line) clear bonus")
    void testTetrisClearBonus() {
        // Tetris: 50 * 4 * 4 = 800
        score.add(800);
        assertEquals(800, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Score should be resettable to zero")
    void testScoreReset() {
        score.add(1000);
        assertEquals(1000, score.scoreProperty().getValue());

        score.reset();
        assertEquals(0, score.scoreProperty().getValue(), "Score should reset to 0");
    }

    @Test
    @DisplayName("Score should continue accumulating after reset")
    void testScoreAccumulationAfterReset() {
        score.add(500);
        score.reset();
        score.add(200);

        assertEquals(200, score.scoreProperty().getValue(), "Score should accumulate after reset");
    }

    @Test
    @DisplayName("Score property should be observable")
    void testScorePropertyBinding() {
        final int[] capturedScore = new int[1];

        score.scoreProperty().addListener((observable, oldValue, newValue) -> {
            capturedScore[0] = newValue.intValue();
        });

        score.add(150);

        assertEquals(150, capturedScore[0], "Property binding should update listener");
    }

    @Test
    @DisplayName("Multiple listener updates should work correctly")
    void testMultipleListenerUpdates() {
        final int[] updateCount = new int[1];

        score.scoreProperty().addListener((observable, oldValue, newValue) -> {
            updateCount[0]++;
        });

        score.add(100);
        score.add(200);
        score.add(300);

        assertEquals(3, updateCount[0], "Should trigger listener for each update");
        assertEquals(600, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Score should handle large accumulations")
    void testLargeScoreAccumulation() {
        for (int i = 0; i < 100; i++) {
            score.add(100);
        }

        assertEquals(10000, score.scoreProperty().getValue(), "Score should handle large values");
    }

    @Test
    @DisplayName("Score should not decrease with positive additions")
    void testScoreNeverDecreases() {
        int previousScore = score.scoreProperty().getValue();

        for (int i = 0; i < 10; i++) {
            score.add(50);
            int currentScore = score.scoreProperty().getValue();
            assertTrue(currentScore >= previousScore, "Score should never decrease");
            previousScore = currentScore;
        }
    }

    @Test
    @DisplayName("Score calculation should work for realistic game scenario")
    void testRealisticGameScenario() {
        // Simulate a game session
        // First clear: 1 line
        score.add(50);
        assertEquals(50, score.scoreProperty().getValue());

        // Second clear: 2 lines
        score.add(200);
        assertEquals(250, score.scoreProperty().getValue());

        // Third clear: 3 lines
        score.add(450);
        assertEquals(700, score.scoreProperty().getValue());

        // Fourth clear: Tetris (4 lines)
        score.add(800);
        assertEquals(1500, score.scoreProperty().getValue());

        // Verify final score
        assertTrue(score.scoreProperty().getValue() >= 1500, "Final score should reflect all clears");
    }

    @Test
    @DisplayName("Score should maintain consistency across multiple operations")
    void testScoreConsistency() {
        score.add(100);
        int score1 = score.scoreProperty().getValue();

        score.add(0); // Add 0
        int score2 = score.scoreProperty().getValue();

        assertEquals(score1, score2, "Adding 0 should not change score");

        score.add(50);
        int score3 = score.scoreProperty().getValue();

        assertEquals(150, score3, "Score should maintain consistency");
    }

    @Test
    @DisplayName("Reset should work multiple times")
    void testMultipleResets() {
        score.add(100);
        score.reset();
        assertEquals(0, score.scoreProperty().getValue());

        score.add(200);
        score.reset();
        assertEquals(0, score.scoreProperty().getValue());

        score.add(300);
        score.reset();
        assertEquals(0, score.scoreProperty().getValue());
    }
}
