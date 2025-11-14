https://github.com/omerrfat/CW2025

Implemented Features and Working Properly:
Pressing P to Pause the game.
Pressing R to restart the game.
Pressing N for a new game.
Pressing space bar for hard drop.
Intuitive Start Up menu screen.
Ghost Shadow for falling bricks.


Modified Java Classes:
1- GameController.java: Introduced 2 new private variables `paused` and `gameOver` to show the paused state of the game. I also added two methods togglePause() and isPaused() to verify and 
return paused and unpaused versions of the game. 
                          In onDownEvent, onLeftEvent, onRightEvent and onRotateEvent, added a line of code that ignores the methods if the game is paused.
                          Added getBoardMatrix(), because board is a private variable, adding a getter for boardMatrix without breaking encapsulation.
2- GuiController.java: Added 2 key events to pause using the letter P and restart the game with the letter R.
                      Added method `moveDownHard()` that implements the logic of pressing the space bar, which pushes the block down instantly.
                      Added Ghost shadow for falling bricks that show where the brick would land.
