package net.ironedge.libraryofiron.event;

public class LoIEvent {

    private boolean cancelled = false;

    /**
     * Some events can be cancelled to prevent default behavior.
     * Not all events have to respect this.
     */
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}