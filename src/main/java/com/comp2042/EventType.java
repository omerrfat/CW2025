package com.comp2042;

/**
 * EventType - Enumeration of possible game move actions.
 * 
 * Values:
 * - DOWN: Move brick down one row
 * - LEFT: Move brick left one column
 * - RIGHT: Move brick right one column
 * - ROTATE: Rotate brick clockwise
 * 
 * Extended events (HARD_DROP, HOLD) can be handled separately.
 * 
 * @author Umer Imran
 * @version 2.0
 */
public enum EventType {
    DOWN, LEFT, RIGHT, ROTATE
}
