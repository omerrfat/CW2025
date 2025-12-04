package com.comp2042.event;

/**
 * EventSource - Enumeration indicating the source of a game move event.
 * 
 * Values:
 * - USER: Player initiated (keyboard input)
 * - THREAD: Automatic (game loop, gravity, etc.)
 * 
 * Used to distinguish between player actions and system-triggered events.
 * 
 * @author Umer Imran
 * @version 2.0
 */
public enum EventSource {
    USER, THREAD
}
