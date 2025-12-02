# Tetris Game Refactoring: Single Responsibility & Design Patterns

## Overview
This refactoring improves code maintainability by adhering to SOLID principles and applying proven design patterns.

## Single Responsibility Principle (SRP)

### Before Refactoring
- **GuiController**: 925 lines handling rendering, input, animations, state management, all mixed together

### After Refactoring

#### 1. **BrickRenderer** (`ui/BrickRenderer.java`)
**Responsibility**: Render bricks with proper colors and positioning
- Creates brick rectangles
- Applies correct colors based on brick type
- Renders bricks on game board and preview panes
- **Benefits**: Reusable brick rendering logic, easy color customization

#### 2. **BoardRenderer** (`ui/BoardRenderer.java`)
**Responsibility**: Initialize and maintain the game board visual representation
- Creates the 25x10 game board grid
- Renders board background cells
- Applies consistent board styling
- **Benefits**: Separates static board from dynamic brick rendering

#### 3. **AnimationManager** (`ui/AnimationManager.java`)
**Responsibility**: Create and manage all game animations
- Line clear animations (fade and scaling)
- Screen shake effects (game over)
- Game over pulse animations
- Pause menu slide-in animations
- Score notification animations
- **Benefits**: Centralizes animation logic, easy to add new animations, isolated from game logic

#### 4. **ScoreManager** (`ui/ScoreManager.java`)
**Responsibility**: Handle score updates and display
- Bind score to UI label
- Track and update high score
- Format score for display
- Persist high score state
- **Benefits**: Encapsulates score logic, reusable across different UI components

#### 5. **InputHandler** (`ui/input/InputHandler.java`)
**Responsibility**: Convert keyboard input to game events
- Map keyboard keys to game events
- Create MoveEvent objects for each input
- Delegate to InputEventListener
- Support hold, hard drop, and other special moves
- **Benefits**: Centralized input handling, easy to add new controls

#### 6. **GameStateManager** (`game/GameStateManager.java`)
**Responsibility**: Manage and notify about game state changes
- Maintain pause/game over/game started states
- Notify listeners of state transitions
- Provide observable properties for UI binding
- **Benefits**: Decouples game state from UI, supports Observer pattern

---

## Design Patterns Applied

### 1. **Strategy Pattern** (InputHandler)
```
InputHandler uses KeyBindingStrategy interface:
- DefaultKeyBindingStrategy: Standard Tetris controls (arrow keys, WASD)
- Can add CustomKeyBindingStrategy: User-defined controls
- Can add AccessibilityKeyBindingStrategy: Accessibility-focused controls

Benefits:
- Runtime customization of key bindings
- Easy to add new control schemes
- No changes to InputHandler needed for new strategies
```

**Usage**:
```java
InputHandler handler = new InputHandler();
handler.setKeyBindingStrategy(new DefaultKeyBindingStrategy());
// Or swap at runtime:
handler.setKeyBindingStrategy(new AccessibilityKeyBindingStrategy());
```

### 2. **Observer Pattern** (GameStateManager)
```
GameStateManager implements Observer pattern:
- GameStateListener: Observer interface
- Multiple listeners can subscribe to state changes
- onGamePaused(), onGameResumed(), onGameOver(), onGameStarted()

Benefits:
- Decouples game logic from UI components
- Multiple listeners react independently
- Easy to add new state change handlers
```

**Usage**:
```java
GameStateManager stateManager = new GameStateManager();
stateManager.setGameStateListener(new GameStateListener() {
    @Override
    public void onGamePaused() {
        // Show pause menu
    }
    @Override
    public void onGameOver() {
        // Show game over screen
    }
});
```

### 3. **Single Responsibility in Rendering**
```
Separation of concerns:
- BrickRenderer: Dynamic brick rendering
- BoardRenderer: Static board initialization
- ColorProvider: Color management (inner class in BrickRenderer)

Benefits:
- Easy to modify rendering independently
- Testable components
- Reusable in different contexts
```

### 4. **Decorator Pattern Potential** (AnimationManager)
```
AnimationManager can be extended with:
- EffectDecorator: Chain animation effects
- SoundEffectDecorator: Add sound to animations
- ParticleEffectDecorator: Add particle effects

Example:
Animation anim = new LineCompleteAnimation()
    .withSoundEffect()
    .withParticleEffect()
    .withScreenShake();
```

---

## Migration Path

### Phase 1: Create New Classes ✓
- BrickRenderer
- AnimationManager
- BoardRenderer
- ScoreManager
- InputHandler with Strategy Pattern
- GameStateManager with Observer Pattern

### Phase 2: Integrate into GuiController
1. Replace inline rendering logic with BrickRenderer
2. Replace inline animations with AnimationManager
3. Replace inline score logic with ScoreManager
4. Replace inline input handling with InputHandler
5. Replace state properties with GameStateManager

### Phase 3: Refactor GameController
1. Use GameStateManager for state management
2. Integrate InputHandler for input processing
3. Separate game logic coordination from event handling

### Phase 4: Testing & Validation
1. Verify compilation
2. Test each new class independently
3. Verify game functionality intact
4. Performance testing

---

## SOLID Principles Demonstrated

### S - Single Responsibility 
- Each class has ONE reason to change
- BrickRenderer only changes if rendering logic changes
- AnimationManager only changes if animations change
- etc.

### O - Open/Closed 
- Open for extension (Strategy pattern allows new key binding strategies)
- Closed for modification (InputHandler doesn't need changes for new controls)

### L - Liskov Substitution 
- KeyBindingStrategy implementations are substitutable
- GameStateListener implementations are substitutable

### I - Interface Segregation 
- Focused interfaces (KeyBindingStrategy, GameStateListener)
- No large fat interfaces

### D - Dependency Inversion 
- Depend on abstractions (KeyBindingStrategy, GameStateListener)
- Not on concrete implementations

---

## Benefits of This Refactoring

1. **Maintainability**: Each class is smaller, easier to understand
2. **Testability**: Can unit test each component independently
3. **Reusability**: Components can be used in other projects
4. **Extensibility**: Easy to add new features using design patterns
5. **Scalability**: Can add new animation types, key bindings, etc. without modifying existing code
6. **Performance**: Can optimize individual components without affecting others

---

## Next Steps

1. Integrate new classes into GuiController gradually
2. Update GameController to use new managers
3. Remove duplicate code from GuiController
4. Add unit tests for each new class
5. Optimize performance if needed
6. Consider adding more design patterns (Factory, Facade, etc.)

---

## Example: Using the New Architecture

```java
// Old way (mixed responsibilities in GuiController)
public class GuiController {
    // 925 lines of rendering, input, animations, state all mixed together
}

// New way (separated concerns)
public class GuiController {
    private BrickRenderer brickRenderer;
    private BoardRenderer boardRenderer;
    private AnimationManager animationManager;
    private ScoreManager scoreManager;
    private InputHandler inputHandler;
    private GameStateManager gameStateManager;
    
    public void initialize(URL location, ResourceBundle resources) {
        brickRenderer = new BrickRenderer();
        boardRenderer = new BoardRenderer();
        animationManager = new AnimationManager();
        scoreManager = new ScoreManager(scoreLabel, highScoreLabel);
        inputHandler = new InputHandler();
        gameStateManager = new GameStateManager();
        
        // Each component handles its specific responsibility
        boardRenderer.initializeBoard(gamePanel);
        inputHandler.setEventListener(eventListener);
        gameStateManager.setGameStateListener(this::onGameStateChanged);
    }
    
    private void onGameStateChanged() {
        // React to state changes
    }
}


Author: Umer Imran
Date: 2nd December 2025
