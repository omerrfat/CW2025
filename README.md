https://github.com/omerrfat/CW2025

Implemented Features and Working Properly:
Pressing P to Pause the game.
Pressing R to restart the game.
Pressing N for a new game.

Modified Java Classes:
1- GameController.java: Introduced 2 new private variables `paused` and `gameOver` to show the paused state of the game. I also added two methods togglePause() and isPaused() to verify and 
return paused and unpaused versions of the game. 
                          In onDownEvent, onLeftEvent, onRightEvent and onRotateEvent, added a line of code that ignores the methods if the game is paused.
2- GuiController.java: Added 2 key events to pause using the letter P and restart the game with the letter R.
