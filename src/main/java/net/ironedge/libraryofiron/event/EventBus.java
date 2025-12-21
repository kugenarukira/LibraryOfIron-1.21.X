package net.ironedge.libraryofiron.event;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe, type-safe EventBus for LoI events.
 */
public final class EventBus {

    // Map of event class -> phase -> list of listeners
    private final Map<Class<? extends LoIEvent>, Map<EventPhase, List<EventListener<?>>>> listeners = new ConcurrentHashMap<>();

    /**
     * Register a listener for a specific event type and phase.
     * Null values are forbidden.
     */
    public <T extends LoIEvent> void register(Class<T> eventType, EventPhase phase, EventListener<T> listener) {
        Objects.requireNonNull(eventType, "Event type cannot be null");
        Objects.requireNonNull(phase, "Event phase cannot be null");
        Objects.requireNonNull(listener, "Event listener cannot be null");

        listeners
                .computeIfAbsent(eventType, k -> new EnumMap<>(EventPhase.class))
                .computeIfAbsent(phase, k -> new CopyOnWriteArrayList<>())
                .add(listener);
    }

    /**
     * Fire an event. All listeners registered for the event class or any superclass are invoked.
     * Listeners respect cancellation: if event.isCancelled() is true, remaining listeners are skipped.
     */
    public void fire(LoIEvent event) {
        Objects.requireNonNull(event, "Event cannot be null");
        Class<?> eventClass = event.getClass();

        // Iterate all registered listener classes
        for (Map.Entry<Class<? extends LoIEvent>, Map<EventPhase, List<EventListener<?>>>> entry : listeners.entrySet()) {
            Class<?> registeredClass = entry.getKey();

            // Only call listeners if the registered class is a superclass of the event
            if (registeredClass.isAssignableFrom(eventClass)) {
                Map<EventPhase, List<EventListener<?>>> phaseMap = entry.getValue();

                // Invoke listeners in phase order
                for (EventPhase phase : EventPhase.values()) {
                    List<EventListener<?>> phaseListeners = phaseMap.get(phase);
                    if (phaseListeners != null) {
                        for (EventListener<?> listener : phaseListeners) {
                            if (event.isCancelled()) break;
                            invokeListener(listener, event);
                        }
                    }
                }
            }
        }
    }

    /**
     * Helper to safely invoke a listener with the correct generic type.
     */
    @SuppressWarnings("unchecked")
    private <T extends LoIEvent> void invokeListener(EventListener<?> listener, LoIEvent event) {
        try {
            ((EventListener<T>) listener).onEvent((T) event);
        } catch (ClassCastException ex) {
            // Should never happen if register() is used correctly
            throw new IllegalStateException("Listener type mismatch for event: " + event.getClass(), ex);
        }
    }
}