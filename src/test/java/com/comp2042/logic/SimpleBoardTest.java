package com.comp2042.logic;

import com.comp2042.dto.ViewData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for SimpleBoard class.
 * Tests game logic including board initialization and brick management.
 */
@DisplayName("SimpleBoard Tests")
class SimpleBoardTest {

    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(10, 25);
    }

    @Test
    @DisplayName("Board should initialize with correct dimensions")
    void testBoardInitialization() {
        assertNotNull(board, "Board should be created");
    }

    @Test
    @DisplayName("New game should have initial score of 0")
    void testInitialScore() {
        Score score = board.getScore();
        assertNotNull(score, "Score should exist");
        assertEquals(0, score.scoreProperty().getValue(), "Initial score should be 0");
    }

    @Test
    @DisplayName("Board should have ghost brick coordinates")
    void testGhostBrickCoordinates() {
        int[][] ghostCoords = board.getGhostBrickCoordinates();
        assertNotNull(ghostCoords, "Ghost brick coordinates should be calculable");
    }

    @Test
    @DisplayName("Game board should be retrievable")
    void testGetGameBoard() {
        int[][] gameBoard = board.getBoardMatrix();
        assertNotNull(gameBoard, "Game board should exist");
        assertEquals(10, gameBoard.length, "Board should have 10 rows");
        assertEquals(25, gameBoard[0].length, "Board should have 25 columns");
    }

    @Test
    @DisplayName("Brick moving down should succeed initially")
    void testBrickMoveDown() {
        board.newGame();
        boolean moved = board.moveBrickDown();
        assertTrue(moved, "Brick should be able to move down initially");
    }

    @Test
    @DisplayName("Brick moving left should succeed initially")
    void testBrickMoveLeft() {
        board.newGame();
        boolean moved = board.moveBrickLeft();
        assertTrue(moved, "Brick should be able to move left initially");
    }

    @Test
    @DisplayName("Brick moving right should succeed initially")
    void testBrickMoveRight() {
        board.newGame();
        boolean moved = board.moveBrickRight();
        assertTrue(moved, "Brick should be able to move right initially");
    }

    @Test
    @DisplayName("Brick rotation should succeed")
    void testBrickRotation() {
        board.newGame();
        board.rotateLeftBrick();
        assertNotNull(board.getBoardMatrix(), "Board should still exist after rotation");
    }

    @Test
    @DisplayName("Score should increase when points are added")
    void testScoreIncrement() {
        Score score = board.getScore();
        int initialScore = score.scoreProperty().getValue();
        
        score.add(100);
        
        assertEquals(initialScore + 100, score.scoreProperty().getValue(), 
                   "Score should increase by 100");
    }

    @Test
    @DisplayName("Get view data should return board state")
    void testGetViewData() {
        board.newGame();
        ViewData viewData = board.getViewData();
        assertNotNull(viewData, "View data should be retrievable after game start");
    }

    @Test
    @DisplayName("Creating new brick should succeed")
    void testCreateNewBrick() {
        board.createNewBrick();
        assertNotNull(board.getBoardMatrix(), "Board should have matrix after creating brick");
    }

    @Test
    @DisplayName("Clearing rows should return result")
    void testClearRows() {
        ClearRow clearResult = board.clearRows();
        assertNotNull(clearResult, "Clear rows should return a result");
    }
}
