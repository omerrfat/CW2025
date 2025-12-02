package com.comp2042;

/**
 * MoveEvent - Immutable event class representing a game move action.
 * 
 * Encapsulates:
 * - The type of move (LEFT, RIGHT, DOWN, ROTATE, HARD_DROP, HOLD)
 * - The source triggering the move (KEYBOARD, AI, etc.)
 * 
 * Used for event-driven brick movement and game logic communication.
 * 
 * @author Umer Imran
 * @version 2.0
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventSource getEventSource() {
        return eventSource;
    }
}
