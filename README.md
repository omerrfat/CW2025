# Tetris Game Implementation - CW2025

Repository: https://github.com/omerrfat/CW2025

---

## Compilation Instructions

### Prerequisites
- **Java Development Kit (JDK)**: Java 25 (installed at `C:\Users\innoc\.jdks\openjdk-25`)
- **Maven**: 3.8.5 or later (included via Maven Wrapper)
- **JavaFX**: 21.0.6 (managed by Maven)

### Compilation Steps

1. **Navigate to project directory:**
   ```bash
   cd C:\Users\innoc\OneDrive\Desktop\Y2\DMS\Class\CW2025
   ```

2. **Set Java environment (Windows PowerShell):**
   ```powershell
   $env:JAVA_HOME = "C:\Users\innoc\.jdks\openjdk-25"
   ```

3. **Clean and compile the project:**
   ```bash
   .\mvnw clean compile
   ```

4. **Run the application:**
   ```bash
   .\mvnw clean javafx:run
   ```

5. **Run tests:**
   ```bash
   .\mvnw test
   ```

### Build Configuration
- **Source/Target**: Java 25
- **Build Tool**: Maven (with Maven Wrapper for cross-platform compatibility)
- **Compiler Plugin**: 3.13.0
- **Test Runner**: Maven Surefire 3.1.2 with JUnit 5
- **JavaFX Plugin**: 0.0.8 for GUI execution

---

## Implemented Features and Working Properly

### Core Game Mechanics
- **Pause/Resume**: Press `P` to toggle pause state; game respects paused state for all input events
- **Game Restart**: Press `R` to restart the current game session
- **New Game**: Press `N` to start a new game
- **Hard Drop**: Press `Space` to instantly drop the current brick to the bottom
- **Soft Drop**: Press `Down Arrow` for gradual brick descent
- **Brick Movement**: `Left Arrow` and `Right Arrow` move the brick horizontally
- **Brick Rotation**: `Up Arrow` or `Z` rotates the brick counter-clockwise

### User Interface
- **Intuitive Start Menu**: Clean startup screen with game options
- **Ghost Shadow**: Visual indicator showing where the brick will land (projection of falling brick)
- **Score Display**: Real-time scoring system that updates on line clears
- **Next Piece Preview**: Display of the upcoming Tetris piece
- **Pause Menu**: Dedicated pause screen with game state preservation
- **Game Over Screen**: Clear end-of-game notification with score display

### Game Logic
- **Collision Detection**: Accurate collision detection with board boundaries and other bricks
- **Line Clearing**: Detection and removal of complete lines with score bonus (50 × n²)
- **Brick Generation**: Random generation of all 7 standard Tetris pieces (I, O, T, S, Z, J, L)
- **Piece Rotation**: Multi-state rotation system for each brick type
- **Hold Piece Feature**: Ability to hold a brick and swap with current piece

---

## Implemented but Not Working Properly

### Known Issues

1. **Game Difficulty Progression**: 
   - Implemented difficulty levels, but the progression mechanism may not transition smoothly
   - Fallback to default difficulty if issues occur

2. **High Score Persistence**: 
   - High score saving to file was attempted but file I/O handling may have edge cases
   - Scores are maintained during current session only

---

## Features Not Implemented

1. **Sound Effects**: No audio implementation due to scope and complexity
2. **Animation Effects**: Limited animation for line clears (basic RowCollapseAnimator exists but not fully polished)
3. **Leaderboard System**: Multi-session high score tracking not fully implemented
4. **Game Themes**: Only single theme available; alternate themes/skins not implemented
5. **AI Opponent Mode**: Automatic opponent gameplay not included
6. **Online Multiplayer**: No network multiplayer support

---

## New Java Classes

### Core Logic Classes
1. **`Score.java`** (`com.comp2042.logic`)
   - Manages game scoring system
   - Observable property for real-time score updates
   - Implements score calculation for line clears and bonus points
   - Location: `src/main/java/com/comp2042/logic/`

2. **`BrickRotator.java`** (`com.comp2042.logic`)
   - Handles brick rotation state management
   - Manages rotation cycles for each brick type
   - Provides current and next rotation shapes
   - Location: `src/main/java/com/comp2042/logic/`

3. **`ClearRow.java`** (`com.comp2042.logic`)
   - Data class for line clear results
   - Contains cleared row count, score bonus, and board state after clearing
   - Location: `src/main/java/com/comp2042/logic/`

4. **`MatrixOperations.java`** (`com.comp2042.logic`)
   - Static utility class for 2D matrix operations
   - Collision detection (intersect method)
   - Matrix copying and merging
   - Line clear detection
   - Location: `src/main/java/com/comp2042/logic/`

5. **`SimpleBoard.java`** (`com.comp2042.logic`)
   - Core Tetris game board implementation
   - 10×25 standard board dimensions
   - Manages brick placement, movement, and collision
   - Implements line clearing and scoring
   - Location: `src/main/java/com/comp2042/logic/`

### Brick Management Classes
6. **`Brick.java`** (`com.comp2042.logic.bricks`) - Interface
   - Contract for all Tetris brick types
   - Defines `getShapeMatrix()` for rotation states
   - Location: `src/main/java/com/comp2042/logic/bricks/`

7. **`BrickGenerator.java`** (`com.comp2042.logic.bricks`) - Interface
   - Contract for brick generation
   - Ensures consistent brick creation across implementations
   - Location: `src/main/java/com/comp2042/logic/bricks/`

8. **`RandomBrickGenerator.java`** (`com.comp2042.logic.bricks`)
   - Implements random Tetris piece generation
   - Returns all 7 standard Tetris pieces (I, O, T, S, Z, J, L)
   - Location: `src/main/java/com/comp2042/logic/bricks/`

### Data Transfer Objects (DTOs)
9. **`ViewData.java`** (`com.comp2042.dto`)
   - Encapsulates game state for view rendering
   - Contains board matrix, current brick position, ghost brick position
   - Location: `src/main/java/com/comp2042/dto/`

10. **`NextShapeInfo.java`** (`com.comp2042.dto`)
    - Provides next piece information to UI
    - Contains shape and rotation data
    - Location: `src/main/java/com/comp2042/dto/`

11. **`MoveEvent.java`** (`com.comp2042.event`)
    - Event class for brick movement actions
    - Location: `src/main/java/com/comp2042/event/`

### Animation & UI Enhancement
12. **`RowCollapseAnimator.java`** (`com.comp2042`)
    - Handles line clearing animation effects
    - Visual feedback for completed lines
    - Location: `src/main/java/com/comp2042/`

13. **`NotificationPanel.java`** (`com.comp2042`)
    - Displays game notifications and messages
    - UI feedback system
    - Location: `src/main/java/com/comp2042/`

14. **`GameOverPanel.java`** (`com.comp2042`)
    - Game over screen and final score display
    - Location: `src/main/java/com/comp2042/`

15. **`PauseMenuController.java`** (`com.comp2042`)
    - JavaFX controller for pause menu UI
    - Resume/restart/quit options
    - Location: `src/main/java/com/comp2042/`

---

## Modified Java Classes

### 1. **GameController.java** (`com.comp2042.game`)
**Changes Made:**
- Added private variables: `paused` (boolean) and `gameOver` (boolean) to track game state
- Added `togglePause()` method to switch pause state
- Added `isPaused()` method to check current pause state
- Modified event handlers (`onDownEvent`, `onLeftEvent`, `onRightEvent`, `onRotateEvent`) to check `isPaused()` before processing input
- Added `getBoardMatrix()` getter to expose board state without breaking encapsulation
- Integrated pause state logic into all movement and rotation controls

**Rationale:** 
Pause functionality required state tracking and input blocking to prevent game manipulation while paused. The `getBoardMatrix()` getter was necessary for providing board state to tests and the view layer.

### 2. **GuiController.java** (`com.comp2042`)
**Changes Made:**
- Added key event handler for `P` key to pause/resume game (calls `GameController.togglePause()`)
- Added key event handler for `R` key to restart current game session
- Added `moveDownHard()` method implementing hard drop logic (space bar functionality)
- Implemented ghost shadow rendering showing brick landing position
- Integrated ghost brick calculation into game loop rendering

**Rationale:** 
User input handling was required for pause and hard drop features. Ghost shadow provides essential visual feedback for improving player experience and game difficulty management. These features enhance playability and user engagement.

### 3. **Board.java** (`com.comp2042.logic`) - Interface
**Changes Made:**
- Added method declarations for pause state management
- Added method declarations for board state retrieval (getters)
- Extended interface contract to support new game features

**Rationale:**
Interface updates ensure all board implementations provide consistent functionality for pause management and state queries.

---

## Test Suite

### Test Files Created
All tests implement JUnit 5 with comprehensive coverage:

1. **BrickRotatorTest.java** (6 tests)
   - Location: `src/test/java/com/comp2042/logic/`
   - Tests rotation state management and shape cycling

2. **RandomBrickGeneratorTest.java** (6 tests)
   - Location: `src/test/java/com/comp2042/logic/bricks/`
   - Validates brick generation and Tetris piece constraints

3. **MatrixOperationsTest.java** (9 tests)
   - Location: `src/test/java/com/comp2042/logic/`
   - Tests collision detection, boundary checking, and matrix operations

4. **SimpleBoardTest.java** (12 tests)
   - Location: `src/test/java/com/comp2042/logic/`
   - Tests game board logic, brick movement, and scoring

5. **ScoreTest.java** (5 tests) - Pre-existing
   - Tests scoring system

**Total Test Coverage**: 38 passing tests
**Build Status**:  BUILD SUCCESS

### Running Tests
```bash
.\mvnw test
```

---

## Unexpected Problems and Solutions

### 1. **JDK Configuration Issue**
**Problem:** Initial setup had JDK 25 configured in IntelliJ IDE but not available on the system. IntelliJ reported "package org.junit.jupiter.api does not exist" errors.

**Solution:** 
- Located actual JDK 25 installation at `C:\Users\innoc\.jdks\openjdk-25`
- Updated `pom.xml` to correctly reference Java 25 (source/target)
- Updated `.idea/misc.xml` to point to openjdk-25
- Verified compilation and tests work correctly via Maven CLI with proper JAVA_HOME environment variable

### 2. **Test Framework API Mismatches**
**Problem:** Initial test files referenced non-existent methods like `Brick.getShape()`, `MatrixOperations.merge()`, and `SimpleBoard.getGameBoard()`.

**Solution:**
- Used grep search to identify actual public API methods in implementation classes
- Discovered correct method names:
  - `Brick.getShapeMatrix()` returns `List<int[][]>` for rotation states
  - `SimpleBoard.getBoardMatrix()` instead of `getGameBoard()`
  - `SimpleBoard.rotateLeftBrick()` instead of `rotateBrick()`
- Rewrote all 4 test files with correct method references

### 3. **Board Dimension Understanding**
**Problem:** Test boundary conditions failed due to misunderstanding of matrix dimensions. SimpleBoard creates `new int[10][25]` which is 10 rows × 25 columns, but tests initially treated it as 25 × 10.

**Solution:**
- Corrected understanding: `matrix[row][col]` where rows = 10, columns = 25
- Updated all boundary tests to use correct coordinates
- Recalculated out-of-bounds conditions based on proper dimensions

### 4. **Collision Detection Algorithm Understanding**
**Problem:** Intersect method uses complex indexing: `brick[j][i]` with `targetX = x + i` and `targetY = y + j`, causing confusion in test coordinate mapping.

**Solution:**
- Carefully traced through the algorithm logic
- Understood that x = row offset, y = column offset
- Created targeted collision test cases with proper filled cell placement
- Verified algorithm behavior with incremental adjustments

### 5. **Null Pointer Exception in getViewData Test**
**Problem:** Test called `board.getViewData()` before game initialization, resulting in null brick reference in BrickRotator.

**Solution:**
- Added `board.newGame()` call before attempting to retrieve view data
- Ensured proper game state initialization in all board-dependent tests

---

## Summary

This Tetris implementation successfully delivers a fully functional game with:
- Complete core game mechanics
- Intuitive user interface with visual feedback
- Comprehensive test suite (38 passing tests)
- Clean architecture with separation of concerns
- Proper encapsulation and API design
- Java 25 compilation with Maven build system

The project demonstrates good software engineering practices including game state management, event-driven architecture, and thorough testing.
