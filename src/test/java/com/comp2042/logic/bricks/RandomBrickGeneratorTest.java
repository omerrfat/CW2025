package com.comp2042.logic.bricks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for RandomBrickGenerator class.
 * Tests that bricks are generated correctly and have valid shapes.
 */
@DisplayName("RandomBrickGenerator Tests")
class RandomBrickGeneratorTest {

    private RandomBrickGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new RandomBrickGenerator();
    }

    @Test
    @DisplayName("Generator should create non-null brick")
    void testGeneratorCreatesBrick() {
        Brick brick = generator.getBrick();
        assertNotNull(brick, "Generator should create a brick");
    }

    @Test
    @DisplayName("Generated brick should have valid shape matrix")
    void testGeneratedBrickHasValidShape() {
        Brick brick = generator.getBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        
        assertNotNull(shapes, "Brick shape matrix should not be null");
        assertTrue(shapes.size() > 0, "Brick should have at least one rotation state");
        
        int[][] firstShape = shapes.get(0);
        assertTrue(firstShape.length > 0, "Shape should have rows");
        assertTrue(firstShape[0].length > 0, "Shape should have columns");
    }

    @Test
    @DisplayName("Multiple calls should generate bricks")
    void testMultipleBricksCanBeDifferent() {
        Brick brick1 = generator.getBrick();
        Brick brick2 = generator.getBrick();
        Brick brick3 = generator.getBrick();
        Brick brick4 = generator.getBrick();
        
        assertNotNull(brick1);
        assertNotNull(brick2);
        assertNotNull(brick3);
        assertNotNull(brick4);
    }

    @Test
    @DisplayName("All generated bricks should have 2D shape arrays")
    void testAllBricksHave2DShapes() {
        for (int i = 0; i < 20; i++) {
            Brick brick = generator.getBrick();
            List<int[][]> shapes = brick.getShapeMatrix();
            
            assertNotNull(shapes, "Shape list should not be null");
            assertTrue(shapes.size() > 0, "Should have rotation states");
            
            for (int[][] shape : shapes) {
                assertTrue(shape.length > 0, "Shape should have rows");
                for (int[] row : shape) {
                    assertTrue(row.length > 0, "Each row should have columns");
                }
            }
        }
    }

    @Test
    @DisplayName("Generated bricks should be valid Tetris pieces (4 blocks)")
    void testBricksHaveValidBlockCounts() {
        for (int i = 0; i < 10; i++) {
            Brick brick = generator.getBrick();
            List<int[][]> shapes = brick.getShapeMatrix();
            
            for (int[][] shape : shapes) {
                // Count non-zero cells
                int blockCount = 0;
                for (int[] row : shape) {
                    for (int cell : row) {
                        if (cell != 0) blockCount++;
                    }
                }
                
                // Tetris pieces have exactly 4 blocks
                assertEquals(4, blockCount, "Tetris brick should have exactly 4 blocks");
            }
        }
    }

    @Test
    @DisplayName("Generator should produce reasonable shape sizes")
    void testBrickShapeSizesAreReasonable() {
        for (int i = 0; i < 10; i++) {
            Brick brick = generator.getBrick();
            List<int[][]> shapes = brick.getShapeMatrix();
            
            for (int[][] shape : shapes) {
                // Tetris pieces fit in 4x4 grid max
                assertTrue(shape.length <= 4, "Brick height should be <= 4");
                for (int[] row : shape) {
                    assertTrue(row.length <= 4, "Brick width should be <= 4");
                }
            }
        }
    }
}
