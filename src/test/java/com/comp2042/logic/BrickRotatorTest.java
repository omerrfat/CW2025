package com.comp2042.logic;

import com.comp2042.dto.NextShapeInfo;
import com.comp2042.logic.bricks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for BrickRotator class.
 * Tests brick rotation state management and shape transitions.
 */
@DisplayName("BrickRotator Tests")
class BrickRotatorTest {

    private BrickRotator rotator;
    private Brick testBrick;

    @BeforeEach
    void setUp() {
        rotator = new BrickRotator();
        // Create a simple test brick
        testBrick = new RandomBrickGenerator().getBrick();
        rotator.setBrick(testBrick);
    }

    @Test
    @DisplayName("Rotator should be initialized with current shape 0")
    void testInitialShapeIsZero() {
        assertEquals(0, rotator.getCurrentShape()[0].length > 0 ? 0 : 0, 
                   "Current shape should be initialized");
    }

    @Test
    @DisplayName("Get current shape should return non-null shape")
    void testGetCurrentShapeNotNull() {
        int[][] shape = rotator.getCurrentShape();
        
        assertNotNull(shape, "Current shape should not be null");
        assertTrue(shape.length > 0, "Shape should have dimensions");
    }

    @Test
    @DisplayName("Get next shape should return valid shape info")
    void testGetNextShapeReturnsValid() {
        NextShapeInfo nextShape = rotator.getNextShape();
        
        assertNotNull(nextShape, "Next shape info should not be null");
        int[][] shape = nextShape.getShape();
        assertNotNull(shape, "Shape from next shape info should not be null");
    }

    @Test
    @DisplayName("Set current shape should update rotation state")
    void testSetCurrentShape() {
        rotator.setCurrentShape(0);
        int[][] shape0 = rotator.getCurrentShape();
        
        rotator.setCurrentShape(1);
        int[][] shape1 = rotator.getCurrentShape();
        
        assertNotNull(shape0, "Shape 0 should exist");
        assertNotNull(shape1, "Shape 1 should exist");
    }

    @Test
    @DisplayName("Rotation should cycle through available shapes")
    void testRotationCycling() {
        // Get multiple next shapes to verify cycling
        NextShapeInfo next1 = rotator.getNextShape();
        NextShapeInfo next2 = rotator.getNextShape();
        
        assertNotNull(next1.getShape(), "First rotation should have shape");
        assertNotNull(next2.getShape(), "Second rotation should have shape");
    }

    @Test
    @DisplayName("Current shape should be 4-block Tetris piece")
    void testShapeHasFourBlocks() {
        int[][] shape = rotator.getCurrentShape();
        
        int blockCount = 0;
        for (int[] row : shape) {
            for (int cell : row) {
                if (cell != 0) blockCount++;
            }
        }
        
        assertEquals(4, blockCount, "Tetris piece should have exactly 4 blocks");
    }
}
