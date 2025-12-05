package com.comp2042.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the Score class.
 * Tests scoring logic for line clears, soft drops, and hard drops.
 */
@DisplayName("Score Tests")
class ScoreTest {

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    @Test
    @DisplayName("Initial score should be 0")
    void testInitialScore() {
        assertEquals(0, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Score should increase after adding points")
    void testAddPoints() {
        score.add(100);
        assertEquals(100, score.scoreProperty().getValue());

        score.add(50);
        assertEquals(150, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Multiple additions should accumulate")
    void testMultipleAdditions() {
        score.add(100);
        score.add(200);
        score.add(300);
        assertEquals(600, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Score should reset to 0")
    void testResetScore() {
        score.add(500);
        assertEquals(500, score.scoreProperty().getValue());

        score.reset();
        assertEquals(0, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Score property should be observable")
    void testScorePropertyIsObservable() {
        assertNotNull(score.scoreProperty());
    }
}
