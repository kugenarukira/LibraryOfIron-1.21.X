package net.ironedge.libraryofiron.event;

public interface EventListener<T extends LoIEvent> {

    /** Called when an event of type T fires */
    void onEvent(T event);

    /** Returns the event type this listener handles */
    Class<T> getEventType();
}
